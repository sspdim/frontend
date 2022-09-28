package com.example.sspdim

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.EditText
import android.os.Bundle
import kotlin.Throws
import org.json.JSONObject
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sspdim.databinding.ActivityMainBinding
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

//    override fun onStart() {
//        super.onStart()
//        val usernameField = findViewById<EditText>(R.id.username)
//        val passwordField = findViewById<EditText>(R.id.password)
//        usernameField.setText("")
//        passwordField.setText("")
//    }
}