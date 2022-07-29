package com.casoft.gbdiary.data.diary

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate

@Entity(tableName = "DiaryItem")
data class DiaryItemEntity(
    @PrimaryKey val day: LocalDate,
    val sticker: List<Int>,
    val contents: String,
    val images: List<String> = listOf(),
    val status: DiaryItemStatus = DiaryItemStatus.ENABLED,
    val isSync: Boolean = false,
)