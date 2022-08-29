package com.casoft.gbdiary.data.database

import androidx.room.TypeConverter
import com.casoft.gbdiary.data.diary.DiaryItemStatus
import com.casoft.gbdiary.data.diary.toDiaryItemStatus

class DiaryItemStatusConverter {

    @TypeConverter
    fun fromInt(value: Int): DiaryItemStatus = value.toDiaryItemStatus()

    @TypeConverter
    fun toInt(status: DiaryItemStatus): Int = status.value
}