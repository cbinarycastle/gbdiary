package com.casoft.gbdiary.data

import androidx.room.TypeConverter
import com.casoft.gbdiary.model.Sticker
import com.google.gson.Gson

class StickerListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<Sticker> =
        gson.fromJson(value, Array<Sticker>::class.java).toList()

    @TypeConverter
    fun toString(stickers: List<Sticker>): String = gson.toJson(stickers)
}