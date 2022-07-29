package com.casoft.gbdiary.data

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class LocalDateConverter {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @TypeConverter
    fun fromString(value: String): LocalDate = LocalDate.parse(value, formatter)

    @TypeConverter
    fun toString(date: LocalDate): String = date.format(formatter)
}