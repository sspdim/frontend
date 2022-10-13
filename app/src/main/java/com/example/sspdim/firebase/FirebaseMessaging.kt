package com.example.sspdim.firebase

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessaging: FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Firebase Message", "Message: ${message.data}")
        message.data["action"]?.let { Log.d("Firebase Message", it) }
        message.data["data"]?.let { Log.d("Firebase Message", it) }
        if (message.data["action"] == "add-friend") {
            Log.d("Firebase Message", "Calling handle friend request")
            message.data["data"]?.let { handleFriendRequest(message) }
        }
        else if (message.data["action"] == "message") {
            Log.d("Firebase Message", "Calling handle message")
            message.data["data"]?.let { handleNewMessage(message) }
        }
        else if (message.data["action"] == "accept-friend") {
            Log.d("Firebase Message", "Calling handle message")
            message.data["data"]?.let { handleAcceptFriendRequest(message) }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Firebase Token", "Token: $token")
    }

    private fun handleFriendRequest(message: RemoteMessage) {
        message.data["data"]?.let { Log.d("handleFriendRequest", it) }
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            message.data.let {
                val intent = Intent("FriendRequest")
                intent.putExtra("username", message.data["data"])
                intent.putExtra("status", "pending")
                Log.d("FirebaseMessageIntent", "data: ${message.data["data"]}")
                broadcaster?.sendBroadcast(intent)
            }
        })
    }

    private fun handleNewMessage(message: RemoteMessage) {
        message.data["data"]?.let { Log.d("handleMessage", it) }
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            message.data.let {
                val intent = Intent("NewMessage")
                intent.putExtra("from", message.data["data"])
                intent.putExtra("message", message.data["message"])
                intent.putExtra("message_id", message.data["message_id"])
                Log.d("FirebaseMessageIntent", "data: ${message.data["data"]}, ${message.data["message"]}")
                broadcaster?.sendBroadcast(intent)
            }
        })
    }

    private fun handleAcceptFriendRequest(message: RemoteMessage) {
        message.data["data"]?.let { Log.d("handleFriendRequest", it) }
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            message.data.let {
                val intent = Intent("FriendRequest")
                intent.putExtra("username", message.data["data"])
                intent.putExtra("status", "accepted")
                Log.d("FirebaseMessageIntent", "data: ${message.data["data"]}")
                broadcaster?.sendBroadcast(intent)
            }
        })
    }
}