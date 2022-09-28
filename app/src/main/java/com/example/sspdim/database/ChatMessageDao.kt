package com.example.sspdim.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sspdim.database.ChatMessage.Companion.MESSAGE_RECEIVED
import com.example.sspdim.database.ChatMessage.Companion.MESSAGE_SENT
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(chatMessage: ChatMessage)

    @Update
    suspend fun update(chatMessage: ChatMessage)

    @Query("select * from chats where friend_id = :friendId order by timestamp asc")
    fun getFriendMessages(friendId: String): Flow<List<ChatMessage>>

    @Query("select * from chats where status = $MESSAGE_SENT")
    fun getQueuedMessages(): Flow<List<ChatMessage>>

    @Query("update chats set status = $MESSAGE_RECEIVED where (timestamp < :timestamp and status = $MESSAGE_SENT)")
    fun updateMessages(timestamp: Int)
}