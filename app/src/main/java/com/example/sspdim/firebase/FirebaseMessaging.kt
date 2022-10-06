package com.example.sspdim.firebase

import android.util.Log
import androidx.room.Room
import com.example.sspdim.SspdimApplication
import com.example.sspdim.database.AppDatabase
import com.example.sspdim.database.Friend
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_PENDING
import com.example.sspdim.network.AddFirebaseTokenRequest
import com.example.sspdim.network.SspdimApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class FirebaseMessaging: FirebaseMessagingService() {

    private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val jsonAdapter = moshi.adapter(FcmMessage::class.java)
    /*private val database = Room.databaseBuilder(
        this,
        AppDatabase::class.java,
        "app_database"
    )
        .build()*/
    private val dbHelper = MyDbHelper(applicationContext)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Firebase Message", "Message: ${message.data}")
        message.data["action"]?.let { Log.d("Firebase Message", it) }
        if (message.data["action"] == "add-friend") {
            Log.d("Firebase Message", "Calling handle request")
            message.data["username"]?.let { handleFriendRequest(it) }
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

    override fun onCreate() {
        super.onCreate()
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

    private fun handleFriendRequest(username: String) {
        val currentTime = System.currentTimeMillis() / 1000
//        val friend = Friend(username, FRIEND_REQUEST_PENDING, currentTime.toInt())
        Log.d("handleRequest", username)
        dbHelper.insertItem(username)
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
}