package com.example.sspdim.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface KeysDao {
    @Insert
    suspend fun insert(keys: Keys)
}