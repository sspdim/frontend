package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.example.sspdim.data.SettingsDataStore
import com.example.sspdim.databinding.FragmentMainBinding
import com.example.sspdim.network.AddFirebaseTokenRequest
import com.example.sspdim.network.SspdimApi
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "MainFragment"

class MainFragment: Fragment() {
    private var isLoggedIn = false
    private var username: String? = null

    private lateinit var settingsDataStore: SettingsDataStore

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "starting login activity")
        initializeFirebase()

        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.isLoggedInPreferenceFlow.asLiveData().observe(viewLifecycleOwner) { value ->
            isLoggedIn = value
            chooseActivity()
        }
    }

    private fun chooseActivity() {
        if (isLoggedIn) {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }
        else {
            Log.d(TAG, "starting login activity")
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext())
        return if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "error")
            false
        }
        else {
            Log.i(TAG, "Google play services updated")
            true
        }
    }

    private fun initializeFirebase() {
        if(!checkGooglePlayServices()) {
            Log.w(TAG, "Device does not have Google Play Services")
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result

            Log.d("MainActivity", "Token = [$token]")
//            if (username != null) {
//                val request = AddFirebaseTokenRequest(username!!, token)
//                runBlocking {
//                    launch {
//                        val response = SspdimApi.retrofitService.addToken(request)
//                        Log.d("NewToken", "[${response.status}] ${response.message}")
//                    }
//                }
//            }
        })
    }
}