package com.casoft.gbdiary.data.backup

import com.casoft.gbdiary.data.diary.Date
import com.casoft.gbdiary.data.diary.DiaryItemEntity
import com.casoft.gbdiary.model.Sticker
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

val BackupDataDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

data class BackupDataItem(
    val day: String,
    val contents: String,
    val images: List<String>,
    val sticker: List<String>,
)

fun BackupDataItem.toDiaryItemEntity(images: List<String>) = DiaryItemEntity(
    date = LocalDate.parse(day, BackupDataDateFormatter).let {
        Date(
            year = it.year,
            month = it.monthValue,
            dayOfMonth = it.dayOfMonth
        )
    },
    sticker = sticker.map { Sticker.valueOf(it) },
    contents = contents,
    images = images,
    isSync = true
)