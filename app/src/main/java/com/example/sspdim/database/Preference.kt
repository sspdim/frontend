package com.example.sspdim.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class Preference(
        @PrimaryKey(autoGenerate = false)
        val preference: String,
        val value: String
)