package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
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
}