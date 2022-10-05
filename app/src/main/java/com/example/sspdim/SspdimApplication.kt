package com.example.sspdim

import android.app.Application
import com.example.sspdim.database.AppDatabase

class SspdimApplication: Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    fun insertFriend() {

    }
}