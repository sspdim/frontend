package com.example.sspdim.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessaging: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Firebase Message", "Message: ${message.data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("Firebase Token", "Token: $token")
    }
}