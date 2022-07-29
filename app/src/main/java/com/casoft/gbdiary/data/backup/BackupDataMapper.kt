package com.casoft.gbdiary.data.backup

import com.casoft.gbdiary.data.diary.DiaryItemEntity
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun BackupDataItem.toDiaryItemEntity(images: List<String>) = DiaryItemEntity(
    day = LocalDate.parse(day, dateFormatter),
    sticker = sticker,
    contents = contents,
    images = images,
    isSync = true
)

fun DiaryItemEntity.toBackupData(images: List<String>) = BackupDataItem(
    day = day.format(dateFormatter),
    contents = contents,
    images = images,
    sticker = sticker
)