package com.example.sspdim.firebase

import android.util.Log
import com.example.sspdim.network.AddFirebaseTokenRequest
import com.example.sspdim.network.SspdimApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FirebaseMessaging: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
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
}