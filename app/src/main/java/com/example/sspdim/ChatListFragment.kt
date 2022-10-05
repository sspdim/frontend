package com.example.sspdim

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentChatListBinding
import com.example.sspdim.model.ChatListAdapter
import com.example.sspdim.model.ChatViewModel
import com.example.sspdim.model.ChatViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "ChatListFragment"

class ChatListFragment: Fragment() {

    private lateinit var settingsDataStore: SettingsDataStore

    private val viewModel: ChatViewModel by activityViewModels {
        ChatViewModelFactory(
            (activity?.application as SspdimApplication).database.friendDao()
        )
    }

    private var _binding: FragmentChatListBinding? = null
    private lateinit var binding: FragmentChatListBinding

    private lateinit var addFriendDialog: MaterialAlertDialogBuilder
    private lateinit var addFriendDialogView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Submit") { _, _ ->
                val res = addFriendDialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
                    R.id.add_friend_text_edit_field
                )
                Log.d(TAG, "${res?.text}")
                if (!res?.text.isNullOrEmpty()) {
                    val resp: LiveData<Int> = viewModel.sendAddFriendRequest(res!!.text.toString())
                    resp.observe(requireActivity()) { value ->
                        Log.d(TAG, "Response from server: ${resp.value}")
                        Toast.makeText(
                            requireContext(),
                            "Friend request sent!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.addFriendActionButton.setOnClickListener {
            onClickButton()
        }
        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.usernamePreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setUsername(value)
        }
        settingsDataStore.serverPreference.asLiveData().observe(viewLifecycleOwner) { value ->
            viewModel.setServer(value)
        }
        val adapter = ChatListAdapter()
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this.context)
        viewModel.chats.observe(this.viewLifecycleOwner) { items ->
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

    private fun onClickChat() {
        // TODO("Handle clicking on a specific chat; navigate to chat fragment")
    }
}