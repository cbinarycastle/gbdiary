package com.casoft.gbdiary.data

import androidx.room.TypeConverter
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.sticker
import com.google.gson.Gson

class StickerListConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String): List<Sticker> {
        return gson.fromJson(value, Array<Int>::class.java)
            .map { it.sticker }
    }

    @TypeConverter
    fun toString(stickers: List<Sticker>): String {
        return stickers.map { it.value }
            .let { gson.toJson(it) }
    }
}