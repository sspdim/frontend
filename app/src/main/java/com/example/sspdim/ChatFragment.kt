package com.example.sspdim

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sspdim.crypto.SessionModel
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_ACCEPTED
import com.example.sspdim.databinding.FragmentChatBinding
import com.example.sspdim.model.ChatMessageAdapter
import com.example.sspdim.model.ChatViewModel
import com.example.sspdim.model.ChatViewModelFactory
import kotlin.properties.Delegates

private const val TAG = "ChatFragment"

class ChatFragment: Fragment() {

    private val navigationArgs: ChatFragmentArgs by navArgs()

    private lateinit var settingsDataStore: SettingsDataStore

    private var _binding: FragmentChatBinding? = null
    private lateinit var binding: FragmentChatBinding

    private lateinit var friendUsername: String
    private var friendStatus by Delegates.notNull<Int>()
    private lateinit var username : String
    private lateinit var server : String

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(
            (activity?.application as SspdimApplication).database.chatMessageDao(),
            navigationArgs.friendUsername
        )
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Received Intent3; ${intent?.extras}")
            if (intent != null) {
                Log.d(TAG, "Adding Message")
                val bundle = intent.extras
                val from = bundle?.getString("from")
                val message = bundle?.getString("message")
                val messageId = bundle?.getString("message_id")
                val timestamp = bundle?.getString("timestamp")
                Log.d(TAG, "From: $from, message: ${message!!.toByteArray()}, message_id: $messageId}")
                try {
                    var sessionModel: SessionModel = SessionModel("$username@$server")
                    var decryptedMessage: String = sessionModel.decrypt(requireContext(), message)
                    Log.d(TAG, "From: $from, message: $decryptedMessage, message_id: $messageId}")
                    if (from != null &&
                        message != null &&
                        messageId != null &&
                        timestamp != null
                    ) {
                        viewModel.addMessage(from, decryptedMessage, messageId, timestamp)
                    }
                }
                catch (e: Exception) {
                    Toast.makeText(requireContext(), "Your server is down", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val bundle = activity?.intent?.extras
        if (bundle != null) {
            Log.d(TAG, "Received Intent1")
            val from = bundle.getString("from")
            val message = bundle.getString("message")
            val messageId = bundle.getString("message_id")
            val timestamp = bundle?.getString("timestamp")
            Log.d(TAG, "From: $from, message: $message, message_id: $messageId}")
            if (from != null &&
                message != null &&
                messageId != null &&
                timestamp != null) {
                viewModel.addMessage(from, message, messageId, timestamp)
            }
        }

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        binding = _binding!!
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.sendButton.setOnClickListener {
            try {
                onClickSend()
            }
            catch (e: Exception) {
                Toast.makeText(requireContext(), "Receiver server down", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        activity?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(messageReceiver,
                IntentFilter("NewMessage")
            )
        }

        val bundle = activity?.intent?.extras
        if (bundle != null) {
            Log.d(TAG, "Received Intent2")
            Log.d(TAG, "Adding message ${activity?.intent?.extras?.getString("message")}")
            val from = bundle.getString("from")
            val message = bundle.getString("message")
            val messageId = bundle.getString("message_id")
            val timestamp = bundle?.getString("timestamp")
            Log.d(TAG, "From: $from, message: $message, message_id: $messageId}")
            if (from != null &&
                message != null &&
                messageId != null &&
                timestamp != null) {
                viewModel.addMessage(from, message, messageId, timestamp)
            }
        }

        friendUsername = navigationArgs.friendUsername
        friendStatus = navigationArgs.friendStatus

        Log.d(TAG, "View created, friend: $friendUsername")

        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.usernamePreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setUsername(value)
            this.username = value
        }
        settingsDataStore.serverPreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setServer(value)
            this.server = value
        }

        val adapter = ChatMessageAdapter()
        binding.chatMessageRecyclerView.adapter = adapter
        binding.chatMessageRecyclerView.layoutManager = LinearLayoutManager(this.context)

        if (friendStatus == FRIEND_REQUEST_ACCEPTED) {
            Log.d(TAG, "$friendUsername: $friendStatus")
            binding.chatMessageRecyclerView.visibility = View.VISIBLE
            binding.enterMessageTextInput.visibility = View.VISIBLE
            binding.sendButton.visibility = View.VISIBLE
            binding.friendRequestPendingTextView.visibility = View.GONE
        }

        viewModel.getChats(friendUsername)

        viewModel.chats.observe(this.viewLifecycleOwner) { items ->
            items.let { it ->
                items.forEach { msg ->
                    viewModel.messageIds.add(msg.messageId)
                }
                adapter.submitList(it)
                binding.chatMessageRecyclerView.scrollToPosition(it.size - 1)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        val pendingRequests = viewModel.getPendingMessages()
        pendingRequests.observe(requireActivity()) {
            it.forEach { request ->
                Log.d(TAG, "Pending request from ${request.fromUsername}")
                try {
                    var sessionModel: SessionModel = SessionModel("$username@$server")
                    var decryptedMessage: String = sessionModel.decrypt(requireContext(), request.messageContent)
                    viewModel.addMessage(request.fromUsername, decryptedMessage, request.messageId, request.timestamp)
                }
                catch (e: Exception) {
                    Toast.makeText(requireContext(), "Your server is down", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver)
    }

    private fun onClickSend() {
        val enteredMessage = binding.enterMessageTextInput.text.toString()
        Log.d(TAG, "Message: $enteredMessage")
        binding.enterMessageTextInput.setText("")
        if (enteredMessage.isNotEmpty()) {
            val resp: LiveData<Int> = viewModel.sendMessage(requireContext(), enteredMessage)
            resp.observe(requireActivity()) { value ->
                Log.d(TAG, "Response from server: ${resp.value}")
            }
        }
    }
}