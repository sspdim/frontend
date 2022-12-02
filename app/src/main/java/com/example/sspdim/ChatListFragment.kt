package com.example.sspdim

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sspdim.backup.BackupAndRestore
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentChatListBinding
import com.example.sspdim.model.ChatListAdapter
import com.example.sspdim.model.ChatListViewModel
import com.example.sspdim.model.ChatListViewModelFactory
import com.example.sspdim.network.AddFirebaseTokenRequest
import com.example.sspdim.network.SspdimApi
import com.example.sspdim.network.isOnline
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "ChatListFragment"

class ChatListFragment: Fragment() {
    private var fcmTokenSent: Boolean = false
    private var isExtraOptionsFabVisible = false

    private lateinit var settingsDataStore: SettingsDataStore

    private val viewModel: ChatListViewModel by activityViewModels {
        ChatListViewModelFactory(
            (activity?.application as SspdimApplication).database.friendDao(),
            (activity?.application as SspdimApplication).database.chatMessageDao()
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
                    if (isOnline(requireContext())) {
                        val resp: LiveData<Int> =
                            viewModel.sendAddFriendRequest(res!!.text.toString())
                        resp.observe(requireActivity()) {
                            Log.d(TAG, "Response from server: ${resp.value}")
                            Toast.makeText(
                                requireContext(),
                                "Friend request sent!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    else {
                        Log.d(TAG, "Server unreachable")
                        Toast.makeText(
                            requireContext(),
                            "Server is unreachable!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                res.setText("")
            }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.extraOptionsButton.shrink()

        binding.addFriendActionButton.setOnClickListener {
            onClickButton()
        }

        binding.logoutButton.setOnClickListener {
            onClickLogoutButton()
        }

        binding.takeBackupButton.setOnClickListener {
            onClickBackupButton()
        }

        binding.restoreBackupButton.setOnClickListener {
            onClickRestoreBackupButton()
        }

        binding.extraOptionsButton.apply {
            setOnClickListener {
                if (isExtraOptionsFabVisible) {
                    shrink()
                    binding.addFriendActionButton.visibility = View.GONE
                    binding.takeBackupButton.visibility = View.GONE
                    binding.restoreBackupButton.visibility = View.GONE
                    binding.logoutButton.visibility = View.GONE

                    binding.addFriendTextview.visibility = View.GONE
                    binding.takeBackupTextview.visibility = View.GONE
                    binding.restoreBackupTextview.visibility = View.GONE
                    binding.logoutTextview.visibility = View.GONE
                    isExtraOptionsFabVisible = false
                }
                else {
                    extend()
                    binding.addFriendActionButton.show()
                    binding.takeBackupButton.show()
                    binding.restoreBackupButton.show()
                    binding.logoutButton.show()

                    binding.addFriendTextview.visibility = View.VISIBLE
                    binding.takeBackupTextview.visibility = View.VISIBLE
                    binding.restoreBackupTextview.visibility = View.VISIBLE
                    binding.logoutTextview.visibility = View.VISIBLE
                    isExtraOptionsFabVisible = true
                }
            }
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
            initializeFirebase()
        }
        settingsDataStore.serverPreference.asLiveData().observe(viewLifecycleOwner) { value ->
            if (value.isNotEmpty()) {
                viewModel.setServer(value)
            }
        }
        settingsDataStore.fcmTokenSentPreference.asLiveData().observe(viewLifecycleOwner) { value ->
            fcmTokenSent = value
            initializeFirebase()
        }

        val adapter = ChatListAdapter (
            { friend ->
            val action =
                ChatListFragmentDirections.actionChatListFragmentToChatFragment(friend.username, friend.status)
            Log.d(TAG, "Clicked on ${friend.username}, navigating")
            this.findNavController().navigate(action)
            },
            { friend ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Warning")
                    .setMessage("This action is not reversible. Are you sure you want to remove this friend?")
                    .setCancelable(false)
                    .setNegativeButton("No") { _, _ -> }
                    .setPositiveButton("Yes") { _, _ ->
                        viewModel.removeFriend(friend.username)
                    }
                    .show()
            },
            { friend ->
                if (isOnline(requireContext())) {
                    viewModel.acceptFriendRequest(friend.username)
                }
                else {
                    Log.d(TAG, "Server unreachable")
                    Toast.makeText(
                        requireContext(),
                        "Server is unreachable!",
                        Toast.LENGTH_LONG
                    ).show()
                }
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        if (isOnline(requireContext())) {
            val pendingRequests = viewModel.getPendingRequests()
            pendingRequests.observe(requireActivity()) {
                it.forEach { request ->
                    Log.d(TAG, "Pending request from ${request.fromUsername}")
                    viewModel.updateFriendsList(request.fromUsername, request.status)
                }
            }
        }
        else {
            Log.d(TAG, "Server unreachable")
            Toast.makeText(
                requireContext(),
                "Server is unreachable!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onClickButton() {
        showAddFriendDialog()
    }

    private fun onClickLogoutButton() {
        Log.d(TAG, "Logout button clicked")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Warning")
            .setMessage("Logging out will delete all user data. This action is not reversible. Are you sure you want to logout?")
            .setCancelable(false)
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                runBlocking {
                    settingsDataStore.saveLoggedInPreference(false, requireContext())
                    settingsDataStore.saveUsernamePreference("", requireContext())
                    settingsDataStore.saveServerPreference("", requireContext())
                    settingsDataStore.saveFcmTokenSentPreference(false, requireContext())
                }
                ((activity?.application) as SspdimApplication).database.clearAllTables()
                viewModel.deleteAllKeys(requireContext())
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickBackupButton() {
        var backupAndRestore = BackupAndRestore(requireContext())
        backupAndRestore.backup()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClickRestoreBackupButton() {
        var backupAndRestore = BackupAndRestore(requireContext())
        backupAndRestore.restore()
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

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "error")
            false
        }
        else {
            Log.i(TAG, "Google play services updated")
            true
        }
    }

    private fun initializeFirebase() {
        if(!checkGooglePlayServices()) {
            Log.w(TAG, "Device does not have Google Play Services")
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d(TAG, "Token = [$token]")
            if (!fcmTokenSent and viewModel.getUsername().isNotEmpty()) {
                val request = AddFirebaseTokenRequest(viewModel.getUsername(), token)
                runBlocking {
                    launch {
                        val response = SspdimApi.retrofitService.addToken(request)
                        Log.d("NewToken", "[${response.status}] ${response.message}")
                    }
                }
                lifecycleScope.launch {
                    settingsDataStore.saveFcmTokenSentPreference(true, requireContext())
                }
            }
        })
    }
}