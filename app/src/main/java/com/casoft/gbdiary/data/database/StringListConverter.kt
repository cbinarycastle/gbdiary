package com.casoft.gbdiary.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson

class StringListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<String> =
        gson.fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun toString(list: List<String>): String = gson.toJson(list)
}