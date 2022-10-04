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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.ActivityMainBinding
import com.example.sspdim.network.AddFirebaseTokenRequest
import com.example.sspdim.network.SspdimApi
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
//    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        startActivity(Intent(this, MainActivity2::class.java))
        Log.d("MainActivity", "Sent token; moving to launch fragment")

//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController

//        supportFragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, MainFragment()).commit()

//        setupActionBarWithNavController(navController)

    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
}