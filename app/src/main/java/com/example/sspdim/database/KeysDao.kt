package com.example.sspdim.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface KeysDao {
    @Insert
    suspend fun insert(keys: Keys)

    @Query("select preKeys from keys")
    fun getPrekeys(): List<String>

    @Query("update keys set preKeys = :preKeys")
    suspend fun updatePrekeys(preKeys: List<String>)
}