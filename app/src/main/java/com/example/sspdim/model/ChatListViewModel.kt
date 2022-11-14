package com.example.sspdim.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.sspdim.database.ChatMessageDao
import com.example.sspdim.database.Friend
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_ACCEPTED
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_PENDING
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_SENT
import com.example.sspdim.database.FriendDao
import com.example.sspdim.network.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "ChatListViewModel"

class ChatListViewModel(private val friendDao: FriendDao,
                        private val chatMessageDao: ChatMessageDao): ViewModel() {

    private var username: String = ""
    private var server: String = ""

    val chatList: LiveData<List<Friend>> = friendDao.getAllFriends().asLiveData()

    fun sendAddFriendRequest(friendUsername: String): LiveData<Int> {
        var response: Response?
        val res = MutableLiveData<Int>()
        viewModelScope.launch {
            try {
                val request = AddFriendRequest("$username@$server", friendUsername)
                Log.d(TAG, "${request.friendUsername}, ${request.username}")
                response = SspdimApi.retrofitService.addFriend(request)
                res.postValue(response?.status)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val currentTime = System.currentTimeMillis() / 1000
        val newFriend = Friend(friendUsername, FRIEND_REQUEST_SENT, currentTime.toInt())
        viewModelScope.launch {
            friendDao.insert(newFriend)
        }
        return res
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setServer(server: String) {
        this.server = server
        Log.d(TAG, "setServer $server")
        setBaseUrl("https://$server")
    }

    fun getUsername(): String {
        return username
    }

    fun acceptFriendRequest(friendUsername: String) {
        val currentTime = System.currentTimeMillis() / 1000
        val newFriend = Friend(friendUsername, FRIEND_REQUEST_ACCEPTED, currentTime.toInt())
        viewModelScope.launch {
            val request = AddFriendRequest("$username@$server", friendUsername)
            Log.d(TAG, "${request.friendUsername}, ${request.username}")
            SspdimApi.retrofitService.acceptFriend(request)
            friendDao.update(newFriend)
        }
    }

    fun declineFriendRequest(friendUsername: String) {
        viewModelScope.launch {
            friendDao.deleteFriend(friendUsername)
        }
    }

    fun updateFriendRequestStatus(friendUsername: String) {
        val currentTime = System.currentTimeMillis() / 1000
        val newFriend = Friend(friendUsername, FRIEND_REQUEST_ACCEPTED, currentTime.toInt())
        viewModelScope.launch {
            friendDao.update(newFriend)
        }
    }

    fun addFriend(friendUsername: String) {
        val currentTime = System.currentTimeMillis() / 1000
        val newFriend = Friend(friendUsername, FRIEND_REQUEST_PENDING, currentTime.toInt())
        viewModelScope.launch {
            Log.d(TAG, "Adding friend ${newFriend.username}")
            friendDao.insert(newFriend)
        }
    }

    fun getPendingRequests(): MutableLiveData<List<PendingFriendRequest>> {
        var response: List<PendingFriendRequest>?
        val res = MutableLiveData<List<PendingFriendRequest>>()
        viewModelScope.launch {
            Log.d(TAG, "Fetching pending friend requests")
            val request = GetPendingFriendRequestsRequest("$username@$server")
            response = SspdimApi.retrofitService.getPendingFriendRequests(request)
            res.postValue(response!!)
        }
        return res
    }

    fun updateFriendsList(friendUsername: String, status: Int) {
        val currentTime = System.currentTimeMillis() / 1000
        val newFriend = Friend(friendUsername, status, currentTime.toInt())
        viewModelScope.launch {
            Log.d(TAG, "Adding friend ${newFriend.username}")
            if (status == FRIEND_REQUEST_PENDING) {
                friendDao.insert(newFriend)
            }
            else if (status == FRIEND_REQUEST_ACCEPTED) {
                friendDao.update(newFriend)
            }
        }
    }

    fun removeFriend(friendUsername: String) {
        viewModelScope.launch {
            Log.d(TAG, "Removing Friend")
            friendDao.deleteFriend(friendUsername)
        }
    }

    fun deleteAllKeys(context: Context) {
        val keysModel = KeysModel(context, username)
        viewModelScope.launch {
            keysModel.deleteAllKeys()
        }
    }
}

class ChatListViewModelFactory(
    private val friendDao: FriendDao,
    private val chatMessageDao: ChatMessageDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatListViewModel(friendDao, chatMessageDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}