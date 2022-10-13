package com.example.sspdim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity(R.layout.activity_chat) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.chat_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}