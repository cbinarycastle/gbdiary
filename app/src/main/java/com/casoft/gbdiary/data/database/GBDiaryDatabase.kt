package com.casoft.gbdiary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.casoft.gbdiary.data.diary.DiaryItemDao
import com.casoft.gbdiary.data.diary.DiaryItemEntity

internal const val DATABASE_NAME = "gbdiary"

@Database(entities = [DiaryItemEntity::class], version = 1)
@TypeConverters(value = [
    IntListConverter::class,
    StringListConverter::class,
    StickerListConverter::class,
    DiaryItemStatusConverter::class
])
abstract class GBDiaryDatabase : RoomDatabase() {

    abstract fun diaryItemDao(): DiaryItemDao
}