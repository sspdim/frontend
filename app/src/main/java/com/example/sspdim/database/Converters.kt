package com.example.sspdim.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class Converters {
    @TypeConverter
    fun listToJson(list: List<String>): String? {
        return Gson().toJson(list)
    }
    @TypeConverter
    fun jsonToList(value: String): List<String> {
        return Gson().fromJson(value, Array<String>::class.java).toList()
    }
}