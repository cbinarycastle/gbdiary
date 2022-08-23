package com.casoft.gbdiary.model

import com.casoft.gbdiary.data.backup.BackupDataDateFormatter
import com.casoft.gbdiary.data.backup.BackupDataItem
import com.casoft.gbdiary.data.diary.Date
import com.casoft.gbdiary.data.diary.DiaryItemEntity
import java.time.LocalDate

const val MAX_NUMBER_OF_IMAGES = 3

data class DiaryItem(
    val date: LocalDate,
    val stickers: List<Sticker>,
    val content: String,
    val images: List<String> = listOf(),
)

fun DiaryItem.toBackupData(images: List<String>) = BackupDataItem(
    day = date.format(BackupDataDateFormatter),
    contents = content,
    images = images,
    sticker = stickers.map { it.name }
)

fun DiaryItem.toDiaryItemEntity() = DiaryItemEntity(
    date = Date(
        year = date.year,
        month = date.monthValue,
        dayOfMonth = date.dayOfMonth
    ),
    stickers = stickers,
    contents = content,
    images = images,
)