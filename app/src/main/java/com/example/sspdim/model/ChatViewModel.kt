package com.example.sspdim.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.sspdim.crypto.SessionModel
import com.example.sspdim.database.ChatMessage
import com.example.sspdim.database.ChatMessage.Companion.MESSAGE_RECEIVED
import com.example.sspdim.database.ChatMessage.Companion.MESSAGE_SENT
import com.example.sspdim.database.ChatMessage.Companion.TYPE_FRIEND_MESSAGE
import com.example.sspdim.database.ChatMessage.Companion.TYPE_MY_MESSAGE
import com.example.sspdim.database.ChatMessageDao
import com.example.sspdim.network.*
import kotlinx.coroutines.launch
import kotlin.random.Random

private const val TAG = "ChatViewModel"

class ChatViewModel(
    private val chatMessageDao: ChatMessageDao,
    private val friendUsername: String
    ): ViewModel() {
    private var username: String = ""
    private var server: String = ""

    lateinit var chats: LiveData<List<ChatMessage>>

    val messageIds: MutableSet<Int> = mutableSetOf()

    fun setUsername(username: String) {
        this.username = username
    }

    fun setServer(server: String) {
        this.server = server
        setBaseUrl("https://$server")
    }

    fun getChats(username: String) {
        chats = chatMessageDao.getFriendMessages(username).asLiveData()
    }

    fun sendMessage(context: Context, messageContent: String): LiveData<Int> {
        var response: Response?
        val res = MutableLiveData<Int>()
        val currentTime = System.currentTimeMillis()
        var randomGenerater = Random(currentTime)
        var messageId = randomGenerater.nextInt(0, Int.MAX_VALUE)
        while (messageIds.contains(messageId)) {
            messageId = randomGenerater.nextInt(0, Int.MAX_VALUE)
        }
        messageIds.add(messageId)
        Log.d(TAG, "sendMessage Message Id: $messageId")
        val newMessage = ChatMessage(
            friendUsername, messageId,
            TYPE_MY_MESSAGE, currentTime.toString(), messageContent, MESSAGE_SENT
        )
        /*
        messageId is random for now. Intention is to maintain counter to help decrypt messages received out of order.
         */
        Log.d(TAG, newMessage.toString())
        val sessionModel: SessionModel = SessionModel(friendUsername)
        val encryptedMessage: String? = sessionModel.encrypt(context, messageContent.toByteArray())
        viewModelScope.launch {
            try {
                val request = SendMessageRequest("$username@$server", friendUsername, encryptedMessage!!, messageId.toString(), currentTime.toString())
                Log.d(TAG, "${request.to}, ${request.from}, ${request.message}")
                response = SspdimApi.retrofitService.sendMessage(request)
                res.postValue(response?.status)
                Log.d(TAG, "Retrofit Done")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModelScope.launch {
            chatMessageDao.insert(newMessage)
            Log.d(TAG, "Room Done")
        }
        return res
    }

    fun addMessage(fromUsername: String, messageContent: String, messageId: String, timestamp: String) {
        val currentTime = System.currentTimeMillis() / 1000
        Log.d(TAG, "addMessage Message Id: $messageId")
        val newMessage = ChatMessage(
            fromUsername, messageId.toInt(),
            TYPE_FRIEND_MESSAGE, timestamp, messageContent, MESSAGE_RECEIVED
        )
        Log.d(TAG, newMessage.toString())
        viewModelScope.launch {
            try {
                chatMessageDao.insert(newMessage)
                Log.d(TAG, "Done")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPendingMessages(): MutableLiveData<List<PendingMessage>> {
        var response: List<PendingMessage>?
        val res = MutableLiveData<List<PendingMessage>>()
        viewModelScope.launch {
            Log.d(TAG, "Fetching pending friend requests")
            val request = GetPendingMessagesRequest("$username@$server")
            response = SspdimApi.retrofitService.getPendingMessages(request)
            res.postValue(response!!)
        }
        return res
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