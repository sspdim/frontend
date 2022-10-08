package com.example.sspdim.model

import android.util.Log
import androidx.lifecycle.*
import com.example.sspdim.database.Friend
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_ACCEPTED
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_PENDING
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_SENT
import com.example.sspdim.database.FriendDao
import com.example.sspdim.network.AddFriendRequest
import com.example.sspdim.network.Response
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.launch

private const val TAG = "ChatListViewModel"

class ChatListViewModel(private val friendDao: FriendDao): ViewModel() {

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

    /*fun getFriendChats(friendUsername: String) {
        try {
            chats = chatMessageDao.getFriendMessages(friendUsername).asLiveData()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(friendUsername: String, messageContent: String) {
        val currentTime = System.currentTimeMillis() / 1000
        val newMessage = ChatMessage(friendUsername, (0..Int.MAX_VALUE).random(),
            TYPE_MY_MESSAGE, currentTime.toInt(), messageContent, MESSAGE_SENT)
        Log.d(TAG, newMessage.toString())
        viewModelScope.launch {
            chatMessageDao.insert(newMessage)
        }
    }*/
}

class ChatListViewModelFactory(
    private val friendDao: FriendDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatListViewModel(friendDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}