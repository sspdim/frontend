package com.example.sspdim

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.sspdim.database.AppDatabase
import com.example.sspdim.database.Friend
import com.example.sspdim.firebase.MyDbHelper
import com.example.sspdim.model.ChatListViewModel
import com.example.sspdim.model.ChatListViewModelFactory
import kotlinx.coroutines.*

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity(R.layout.activity_chat) {

    private lateinit var navController: NavController

    /*private val dbHelper = MyDbHelper(this)

    private val viewModel: ChatListViewModel by viewModels {
        ChatListViewModelFactory(
            (application as SspdimApplication).database.friendDao()
        )
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Received Intent; ${intent}")
            if (intent != null) {
                Log.d(TAG, "Adding friend")
                intent.extras?.getString("username")?.let {
                    Log.d(TAG, "Adding friend ${intent.extras?.getString("username")}")
                    addFriend(it)
                }
            }
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val temp = (application as SspdimApplication).database.friendDao().getAllFriends()

        Log.d(TAG, temp.toString())

        val bundle = intent?.extras
        if (bundle != null) {
            Log.d(TAG, "Received Intent")
            Log.d(TAG, "Adding friend ${intent.extras?.getString("username")}")
            bundle.getString("username")?.let { addFriend(it) }
        }

        viewModel.chatList.observe(viewLifecycleOwner) { items ->
            items.let {
                Log.d(TAG, "Chat list: ${it}")
            }
        }*/

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.chat_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    /*override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
            IntentFilter("FriendRequest"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            messageReceiver, IntentFilter("FriendRequest")
        )
    }*/

    @OptIn(DelicateCoroutinesApi::class)
    private fun addFriend(friendUsername: String) {
//        viewModel.addFriend(friendUsername)
        /*val currentTime = System.currentTimeMillis() / 1000
        val newFriend = Friend(friendUsername, Friend.FRIEND_REQUEST_PENDING, currentTime.toInt())
        Log.d(TAG, "Friend: $newFriend")
        val context = this
        dbHelper.insertItem(friendUsername)
        runBlocking {
            suspend {
//                (application as SspdimApplication).database.friendDao().insert(newFriend)
                dbHelper.insertItem(friendUsername)
                Log.d(TAG, "added friend")
            }
        }*/
    }
}