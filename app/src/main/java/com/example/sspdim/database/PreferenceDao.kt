package com.example.sspdim.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(preference: Preference)

    @Update
    suspend fun update(preference: Preference)

    @Delete
    suspend fun delete(preference: Preference)

    @Query("select * from preferences where preference = :key")
    fun getPreference(key: String): Flow<Preference>?
}