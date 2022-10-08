package com.example.sspdim.firebase

import android.content.ContentValues
import android.content.Context
import android.content.SyncRequest
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sspdim.database.Friend
import com.example.sspdim.database.Friend.Companion.FRIEND_REQUEST_PENDING

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, "app_database", null, 1) {

    private val CREATE_TABLE =
        "create table friends(username varchar primary key, status integer, " +
                "last_interaction_timestamp integer)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        onCreate(db)
    }

    fun insertItem(friendUsername: String) {
        val currentTime = System.currentTimeMillis() / 1000
        val query = "insert into friends values (?, ?, ?)"
        val db = writableDatabase
//        db.execSQL(query, arrayOf(friendUsername, FRIEND_REQUEST_PENDING, currentTime))
//        db.close()
        val contentValues = ContentValues()
        contentValues.put("username", friendUsername)
        contentValues.put("status", FRIEND_REQUEST_PENDING)
        contentValues.put("last_interaction_timestamp", currentTime.toInt())
        db.insert("friends", null, contentValues)
        db.close()
    }
}