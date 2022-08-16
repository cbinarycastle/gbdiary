package com.casoft.gbdiary.model

import com.casoft.gbdiary.R
import com.casoft.gbdiary.data.backup.BackupDataDateFormatter
import com.casoft.gbdiary.data.backup.BackupDataItem
import com.casoft.gbdiary.data.diary.Date
import com.casoft.gbdiary.data.diary.DiaryItemEntity
import org.threeten.bp.LocalDate

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

enum class Sticker(val type: StickerType) {
    SMILE(StickerType.MOOD),
    HAPPINESS(StickerType.MOOD),
    HOPEFUL(StickerType.MOOD),
    SATISFACTION(StickerType.MOOD),
    JOY(StickerType.MOOD),
    SADNESS(StickerType.MOOD),
    PANIC(StickerType.MOOD),
    SLEEPINESS(StickerType.MOOD),
    ANGER(StickerType.MOOD),
    DEPRESSION(StickerType.MOOD),
    UNPLEASANT(StickerType.MOOD),
    IMPASSIVE(StickerType.MOOD),
    SICK(StickerType.MOOD),
    CONFUSION(StickerType.MOOD),
    TIRED(StickerType.MOOD),
}

enum class StickerType(val text: String) {
    MOOD(text = "기분"),
    DAILY(text = "일상"),
}

val Sticker.imageResId
    get() = when (this) {
        Sticker.SMILE -> R.drawable.smile
        Sticker.HAPPINESS -> R.drawable.happiness
        Sticker.HOPEFUL -> R.drawable.hopeful
        Sticker.SATISFACTION -> R.drawable.satisfaction
        Sticker.JOY -> R.drawable.joy
        Sticker.SADNESS -> R.drawable.sadness
        Sticker.PANIC -> R.drawable.panic
        Sticker.SLEEPINESS -> R.drawable.sleepiness
        Sticker.ANGER -> R.drawable.anger
        Sticker.DEPRESSION -> R.drawable.depression
        Sticker.UNPLEASANT -> R.drawable.unpleasant
        Sticker.IMPASSIVE -> R.drawable.impassive
        Sticker.SICK -> R.drawable.sick
        Sticker.CONFUSION -> R.drawable.confusion
        Sticker.TIRED -> R.drawable.tired
    }