package com.example.sspdim.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sspdim.MainActivity
import com.example.sspdim.R
import com.example.sspdim.crypto.SessionModel
import com.example.sspdim.data.SettingsDataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class FirebaseMessaging: FirebaseMessagingService() {
    private val channelId = "Notif_Channel"
    private val channelName = "com.example.sspdim.firebase"

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
                val settingsDataStore = SettingsDataStore(applicationContext)
                val username = runBlocking { settingsDataStore.usernamePreference.first() }
                val server = runBlocking { settingsDataStore.serverPreference.first() }
                val session = SessionModel("$username@$server")
                val decryptedMessage = session.decryptNotification(applicationContext,message.data["message"]!!)
                val from = message.data["data"]
                generateNotification(from!!, decryptedMessage)
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

    fun generateNotification(title: String, message:String){
        var randomGenerater = Random(System.currentTimeMillis() / 1000)
        var notificationId = randomGenerater.nextInt(0, Int.MAX_VALUE)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE)
        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,channelId)
            .setSmallIcon(R.drawable.notificationicon)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(notificationId,builder.build())
    }
}