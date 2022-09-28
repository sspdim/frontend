package com.example.sspdim.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(friend: Friend)

    @Update
    suspend fun update(friend: Friend)

    @Delete
    suspend fun delete(friend: Friend)

    @Query("select * from friends order by last_interaction_timestamp desc")
    fun getAllFriends(): Flow<List<Friend>>
}