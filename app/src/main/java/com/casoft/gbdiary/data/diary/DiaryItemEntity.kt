package com.casoft.gbdiary.data.diary

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import java.time.LocalDate

@Entity(tableName = "DiaryItem")
data class DiaryItemEntity(
    @PrimaryKey @Embedded val date: Date,
    val stickers: List<Sticker>,
    val contents: String,
    val images: List<String> = listOf(),
    val status: DiaryItemStatus = DiaryItemStatus.ENABLED,
    val isSync: Boolean = false,
)

fun DiaryItemEntity.toDiaryItem() = DiaryItem(
    date = date.toLocalDate(),
    stickers = stickers,
    content = contents,
    images = images
)

data class Date(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
)

fun Date.toLocalDate() = LocalDate.of(year, month, dayOfMonth)