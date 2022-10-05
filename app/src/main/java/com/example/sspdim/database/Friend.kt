package com.example.sspdim.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "status_pending")
    val statusPending: Boolean,
    @ColumnInfo(name = "last_interaction_timestamp")
    val lastInteractionTimestamp: Int
)