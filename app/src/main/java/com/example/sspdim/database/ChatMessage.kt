package com.example.sspdim.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "chats", primaryKeys = ["friend_username", "message_id", "sender_username"])
data class ChatMessage (
    @ColumnInfo(name = "friend_username")
    val friendUsername: String,
    @ColumnInfo(name = "message_id")
    val messageId: Long,
    @ColumnInfo(name = "sender_username")
    val senderUsername: Int,
    @ColumnInfo(name = "timestamp")
    val timestamp: Int,
    @ColumnInfo(name = "status")
    val status: Int
) {
    companion object {
        const val TYPE_MY_MESSAGE = 0
        const val TYPE_FRIEND_MESSAGE = 1
        const val MESSAGE_SENT = 0
        const val MESSAGE_RECEIVED = 1
    }
}