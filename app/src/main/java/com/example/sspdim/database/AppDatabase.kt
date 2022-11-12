package com.example.sspdim.database

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Friend::class, ChatMessage::class, Keys::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun friendDao(): FriendDao

    abstract fun chatMessageDao(): ChatMessageDao

    abstract fun keysDao() : KeysDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            var converter = Converters()
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addTypeConverter(converter)
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}