package com.example.sspdim.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.sspdim.database.Preference
import com.example.sspdim.database.PreferenceDao

class MainViewModel(private val preferenceDao: PreferenceDao): ViewModel() {

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val SERVER = "server"
        private const val USERNAME = "username"
    }

    fun getIsLoggedIn(): LiveData<Preference>? {
        return preferenceDao.getPreference(IS_LOGGED_IN)?.asLiveData()
    }

    fun getUsername(): LiveData<Preference>? {
        return preferenceDao.getPreference(USERNAME)?.asLiveData()
    }

    fun getServer(): LiveData<Preference>? {
        return preferenceDao.getPreference(SERVER)?.asLiveData()
    }
}

class MainViewModelFactory(private val preferenceDao: PreferenceDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(preferenceDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}