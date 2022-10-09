package com.example.sspdim.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sspdim.ChatInterface
import com.example.sspdim.R
import com.example.sspdim.network.AddFirebaseTokenRequest
import com.example.sspdim.network.SspdimApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FirebaseMessaging: FirebaseMessagingService() {
    private val channelId = "Notif_Channel"
    private val channelName = "com.example.sspdim.firebase"

    private var broadcaster: LocalBroadcastManager? = null
    /*private val database = Room.databaseBuilder(
        this,
        AppDatabase::class.java,
        "app_database"
    )
        .build()*/
//    private val dbHelper = MyDbHelper(applicationContext)

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
        /*val request = AddFirebaseTokenRequest("ploij0", token)
        runBlocking {
            launch {
                val response = SspdimApi.retrofitService.addToken(request)
                Log.d("NewToken", "[${response.status}] ${response.message}]")
            }
        }*/
    }

    /*private fun handleFriendRequest2(username: String) {
        val currentTime = System.currentTimeMillis() / 1000
        val friend = Friend(username, FRIEND_REQUEST_PENDING, currentTime.toInt())
        val dbHelper = MyDbHelper(baseContext)
        val db = dbHelper.writableDatabase
        val cursor = db.query()
        Log.d("handleRequest", "$friend")
        runBlocking {
            try {
                database.friendDao().insert(friend)
                Log.d("handleRequest", "Added $friend")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }*/

    private fun handleFriendRequest(message: RemoteMessage) {
        val currentTime = System.currentTimeMillis() / 1000
//        val friend = Friend(username, FRIEND_REQUEST_PENDING, currentTime.toInt())
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
//        dbHelper.insertItem(username)
        /*runBlocking {
            try {
                database.friendDao().insert(friend)
                Log.d("handleRequest", "Added $friend")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }*/
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
//        dbHelper.insertItem(username)
        /*runBlocking {
            try {
                database.friendDao().insert(friend)
                Log.d("handleRequest", "Added $friend")
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }*/
    }

    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView = RemoteViews("com.example.sspdim.firebase",R.layout.notification)
        remoteView.setTextViewText(R.id.NotificationTitle,title)
        remoteView.setTextViewText(R.id.NotificationContent,message)
        remoteView.setImageViewResource(R.id.app_logo,R.drawable.notificationicon)
        return remoteView
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun generateNotification(title: String, message:String){
        val intent = Intent(this, ChatInterface::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,channelId)
            .setSmallIcon(R.drawable.notificationicon)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        builder = builder.setContent(getRemoteView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0,builder.build())
    }
}