package com.example.sspdim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import com.example.sspdim.data.SettingsDataStore

class MainFragment: Fragment() {
    private lateinit var settingsDataStore: SettingsDataStore
    private var isLoggedIn = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsDataStore = SettingsDataStore(requireContext())
        settingsDataStore.preferenceFlow.asLiveData().observe(viewLifecycleOwner) { value ->
            isLoggedIn = value
            chooseActivity()
        }
    }

    private fun chooseActivity() {
        isLoggedIn = false
        if (isLoggedIn) {
            startActivity(Intent(requireContext(), ChatInterface::class.java))
        }
        else {
            Log.d("af", "starting login activity")
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

}