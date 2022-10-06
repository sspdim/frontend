package com.example.sspdim.model

import android.util.Log
import androidx.lifecycle.*
import com.example.sspdim.database.ChatMessage
import com.example.sspdim.database.ChatMessage.Companion.MESSAGE_SENT
import com.example.sspdim.database.ChatMessage.Companion.TYPE_MY_MESSAGE
import com.example.sspdim.database.ChatMessageDao
import kotlinx.coroutines.launch

private const val TAG = "ChatViewModel"

class ChatViewModel(
    private val chatMessageDao: ChatMessageDao,
    friendUsername: String
    ): ViewModel() {
    private var username: String = ""
    private var server: String = ""

    val chats: LiveData<List<ChatMessage>> = chatMessageDao.getFriendMessages(friendUsername).asLiveData()

    fun setUsername(username: String) {
        this.username = username
    }

    fun setServer(server: String) {
        this.server = server
    }

    fun sendMessage(friendUsername: String, messageContent: String) {
        val currentTime = System.currentTimeMillis() / 1000
        val newMessage = ChatMessage(
            friendUsername, (0..Int.MAX_VALUE).random(),
            TYPE_MY_MESSAGE, currentTime.toInt(), messageContent, MESSAGE_SENT
        )
        /*
        messageId is random for now. Intention is to maintain counter to help decrypt messages received out of order.
         */
        Log.d(TAG, newMessage.toString())
        viewModelScope.launch {
            chatMessageDao.insert(newMessage)
        }
    }
}

class ChatViewModelFactory(
    private val chatMessageDao: ChatMessageDao,
    private val friendUsername: String
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(chatMessageDao, friendUsername) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}