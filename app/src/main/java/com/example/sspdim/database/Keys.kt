package com.example.sspdim.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import org.whispersystems.libsignal.state.PreKeyRecord

@Entity(tableName = "keys", primaryKeys = ["registrationId"])
data class Keys(
    @ColumnInfo(name = "identityKeyPair")
    val identityKeyPair: ByteArray,
    @ColumnInfo(name = "registrationId")
    val registrationId: Int,
//    @ColumnInfo(name = "preKeys")
//    val preKeys: List<ByteArray>,
    @ColumnInfo(name = "signedPreKey")
    val signedPreKey: ByteArray
)