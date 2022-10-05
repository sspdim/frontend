package com.example.sspdim.model

import android.util.Log
import android.widget.ViewSwitcher.ViewFactory
import androidx.lifecycle.*
import com.example.sspdim.database.Friend
import com.example.sspdim.database.FriendDao
import com.example.sspdim.network.AddFriendRequest
import com.example.sspdim.network.Response
import com.example.sspdim.network.SspdimApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "ChatViewModel"

class ChatViewModel(private val friendDao: FriendDao): ViewModel() {

    private var username: String = ""
    private var server: String = ""

//    private val _chats = MutableLiveData<List<Friend>>()
    val chats: LiveData<List<Friend>> = friendDao.getAllFriends().asLiveData()

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
        return res
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setServer(server: String) {
        this.server = server
    }

    fun acceptFriendRequest() {

    }
}

class ChatViewModelFactory(private val friendDao: FriendDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(friendDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}