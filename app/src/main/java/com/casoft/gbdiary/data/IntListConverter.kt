package com.casoft.gbdiary.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class IntListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<Int> =
        gson.fromJson(value, Array<Int>::class.java).toList()

    @TypeConverter
    fun toString(list: List<Int>): String = gson.toJson(list)
}