package com.example.sspdim.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Keys::class], version = 1, exportSchema = false)
abstract class KeysDatabase: RoomDatabase() {

    abstract fun keysDao(): KeysDao

    companion object {
        @Volatile
        private var INSTANCE: KeysDatabase? = null

        fun getKeyDatabase(context: Context): KeysDatabase {
            var converter = Converters()
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeysDatabase::class.java,
                    "keys-db"
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