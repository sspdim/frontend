package com.example.sspdim.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend (
    @PrimaryKey(autoGenerate = false)
    val username: String,
    @ColumnInfo(name = "status")
    val status: Int,
    @ColumnInfo(name = "last_interaction_timestamp")
    val lastInteractionTimestamp: Int
) {
    companion object {
        const val FRIEND_REQUEST_SENT = 0
        const val FRIEND_REQUEST_DELIVERED = 1
        const val FRIEND_REQUEST_ACCEPTED = 2
        const val FRIEND_REQUEST_PENDING = 3
    }
}