package com.casoft.gbdiary.model

import com.casoft.gbdiary.data.backup.BackupDataDateFormatter
import com.casoft.gbdiary.data.backup.BackupDataItem
import com.casoft.gbdiary.data.diary.Date
import com.casoft.gbdiary.data.diary.DiaryItemEntity
import java.io.File
import java.time.LocalDate

const val MAX_IMAGES_FOR_STANDARD_USER = 3
const val MAX_IMAGES_FOR_PREMIUM_USER = 5

data class DiaryItem(
    val date: LocalDate,
    val stickers: List<Sticker>,
    val content: String,
    val images: List<File> = listOf(),
)

fun DiaryItem.toBackupDataItem(imageIds: List<String>) = BackupDataItem(
    day = date.format(BackupDataDateFormatter),
    contents = content,
    images = imageIds,
    sticker = stickers.map { it.backupValue }
)

fun DiaryItem.toDiaryItemEntity() = DiaryItemEntity(
    date = Date(
        year = date.year,
        month = date.monthValue,
        dayOfMonth = date.dayOfMonth
    ),
    stickers = stickers,
    contents = content,
    images = images.map { it.path },
)