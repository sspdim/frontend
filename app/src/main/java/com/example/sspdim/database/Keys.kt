package com.example.sspdim.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters

@Entity(tableName = "keys", primaryKeys = ["registrationId"])
data class Keys(
    @ColumnInfo(name = "identityKeyPair")
    val identityKeyPair: ByteArray,
    @ColumnInfo(name = "registrationId")
    val registrationId: Int,
    @field:TypeConverters(Converters::class)
    @ColumnInfo(name = "preKeys")
    val preKeys: List<String>,
    @ColumnInfo(name = "signedPreKey")
    val signedPreKey: ByteArray
)