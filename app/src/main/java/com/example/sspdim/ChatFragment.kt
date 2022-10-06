package com.example.sspdim

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.database.ChatMessage
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_ACCEPTED
import com.example.sspdim.databinding.FragmentChatBinding
import com.example.sspdim.model.ChatMessageAdapter
import com.example.sspdim.model.ChatListViewModel
import com.example.sspdim.model.ChatViewModel
import com.example.sspdim.model.ChatViewModelFactory
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

private const val TAG = "ChatFragment"

class ChatFragment: Fragment() {

    private val navigationArgs: ChatFragmentArgs by navArgs()

    private lateinit var settingsDataStore: SettingsDataStore

    private var _binding: FragmentChatBinding? = null
    private lateinit var binding: FragmentChatBinding

    private lateinit var friendUsername: String
    private var friendStatus by Delegates.notNull<Int>()

    private val viewModel: ChatViewModel by activityViewModels {
        ChatViewModelFactory(
            (activity?.application as SspdimApplication).database.chatMessageDao(),
            navigationArgs.friendUsername
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        binding = _binding!!
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.sendButton.setOnClickListener {
            onClickSend()
        }

        friendUsername = navigationArgs.friendUsername
        friendStatus = navigationArgs.friendStatus

        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.usernamePreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setUsername(value)
        }
        settingsDataStore.serverPreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setServer(value)
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
        viewModel.chats.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }

    private fun onClickSend() {
        val enteredMessage = binding.enterMessageTextInput.text.toString()
        Log.d(TAG, "Message: $enteredMessage")
        if (enteredMessage.isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.sendMessage(friendUsername, enteredMessage)
            }
        }
    }
}