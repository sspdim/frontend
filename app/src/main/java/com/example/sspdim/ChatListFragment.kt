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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentChatListBinding
import com.example.sspdim.model.ChatListAdapter
import com.example.sspdim.model.ChatListViewModel
import com.example.sspdim.model.ChatListViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "ChatListFragment"

class ChatListFragment: Fragment() {

    private lateinit var settingsDataStore: SettingsDataStore

    private val viewModel: ChatListViewModel by activityViewModels {
        ChatListViewModelFactory(
            (activity?.application as SspdimApplication).database.friendDao()
        )
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Received Intent; ${intent}")
            if (intent != null) {
                Log.d(TAG, "Adding friend")
                intent.extras?.getString("username")?.let {
                    Log.d(TAG, "Adding friend ${intent.extras?.getString("username")}")
                    if (intent.extras?.getString("status") == "pending") {
                        addFriend(it)
                    }
                    else if (intent.extras?.getString("status") == "accepted") {
                        updateFriendStatus(it)
                    }
                }
            }
        }
    }

    private var _binding: FragmentChatListBinding? = null
    private lateinit var binding: FragmentChatListBinding

    private lateinit var addFriendDialog: MaterialAlertDialogBuilder
    private lateinit var addFriendDialogView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val bundle = activity?.intent?.extras
        if (bundle != null) {
            Log.d(TAG, "Received Intent")
            bundle.getString("data")?.let {
                if (bundle.getString("status") == "pending") {
                    addFriend(it)
                }
                else if (bundle.getString("status") == "accepted") {
                    updateFriendStatus(it)
                }
            }
        }
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        binding = _binding!!
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        addFriendDialogView = inflater.inflate(R.layout.add_friend_input_layout, container, false)
        addFriendDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Friend")
            .setMessage("Enter the username:")
            .setCancelable(false)
            .setView(addFriendDialogView)
            .setNegativeButton("Cancel") { _, _ ->
                val res = addFriendDialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    R.id.add_friend_text_edit_field
                )
                res.setText("")
            }
            .setPositiveButton("Submit") { _, _ ->
                val res = addFriendDialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    R.id.add_friend_text_edit_field
                )
                Log.d(TAG, "${res?.text}")
                if (!res?.text.isNullOrEmpty()) {
                    val resp: LiveData<Int> = viewModel.sendAddFriendRequest(res!!.text.toString())
                    resp.observe(requireActivity()) {
                        Log.d(TAG, "Response from server: ${resp.value}")
                        Toast.makeText(
                            requireContext(),
                            "Friend request sent!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                res.setText("")
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addFriendActionButton.setOnClickListener {
            onClickButton()
        }

        activity?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(messageReceiver,
                IntentFilter("FriendRequest"))
        }

        val bundle = activity?.intent?.extras
        if (bundle != null) {
            Log.d(TAG, "Received Intent")
            Log.d(TAG, "Adding friend ${activity?.intent?.extras?.getString("username")}")
            bundle.getString("username")?.let {
                if (bundle.getString("status") == "pending") {
                    addFriend(it)
                }
                else if (bundle.getString("status") == "accepted") {
                    updateFriendStatus(it)
                }
            }
        }

        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.usernamePreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setUsername(value)
        }
        settingsDataStore.serverPreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setServer(value)
        }

        val adapter = ChatListAdapter (
            { friend ->
            val action =
                ChatListFragmentDirections.actionChatListFragmentToChatFragment(friend.username, friend.status)
            this.findNavController().navigate(action)
            },
            { friend ->
                viewModel.acceptFriendRequest(friend.username)
            },
            { friend ->
                viewModel.declineFriendRequest(friend.username)
            }
        )

        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this.context)

        viewModel.chatList.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }

    private fun onClickButton() {
        showAddFriendDialog()
    }

    private fun showAddFriendDialog() {
        try {
            if (addFriendDialogView.parent != null) {
                (addFriendDialogView.parent as ViewGroup).removeView(addFriendDialogView)
            }
            addFriendDialog
                .show()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver)
    }

    private fun addFriend(friendUsername: String) {
        Log.d(TAG, "Adding friend $friendUsername")
        viewModel.addFriend(friendUsername)
        Log.d(TAG, "Added friend $friendUsername")
    }

    private fun updateFriendStatus(friendUsername: String) {
        viewModel.updateFriendRequestStatus(friendUsername)
    }

    private fun onClickChat() {
        // TODO("Handle clicking on a specific chat; navigate to chat fragment")
    }
}