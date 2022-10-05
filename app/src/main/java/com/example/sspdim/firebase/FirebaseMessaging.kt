package com.example.sspdim.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViews.RemoteView
import androidx.core.app.NotificationCompat
import com.example.sspdim.ChatInterface
import com.example.sspdim.LoginFragment
import com.example.sspdim.MainActivity
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
    override fun onMessageReceived(message: RemoteMessage) {
        if (message.getNotification()!=null){

            generateNotification(message.notification!!.title!!, message.notification!!.body!!)

        }
        //super.onMessageReceived(message)
        Log.d("Firebase Message", "Message: ${message.data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("Firebase Token", "Token: $token")
        val request = AddFirebaseTokenRequest("tusr", token)
        runBlocking {
            launch {
                val response = SspdimApi.retrofitService.addToken(request)
                Log.d("NewToken", "[${response.status}] ${response.message}]")
            }
        }
    }

    fun getRemoteView(title: String,message: String): RemoteViews{

        val remoteView = RemoteViews("com.example.sspdim.firebase",R.layout.notification)
        remoteView.setTextViewText(R.id.NotificationTitle,title)
        remoteView.setTextViewText(R.id.NotificationContent,message)
        remoteView.setImageViewResource(R.id.app_logo,R.drawable.notificationicon)

        return remoteView

    }

    fun generateNotification(title: String,message:String){

        val intent = Intent(this,ChatInterface::class.java)
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