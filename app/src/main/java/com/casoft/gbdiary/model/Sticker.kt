package com.casoft.gbdiary.model

import com.casoft.gbdiary.R

private const val MOOD_PREFIX = "mood_"
private const val DAILY_PREFIX = "daily_"

enum class Sticker(
    val value: Int,
    val type: StickerType,
    val backupValue: String,
) {
    SMILE(1, StickerType.MOOD, "${MOOD_PREFIX}1"),
    HAPPINESS(2, StickerType.MOOD, "${MOOD_PREFIX}2"),
    HOPEFUL(3, StickerType.MOOD, "${MOOD_PREFIX}3"),
    SATISFACTION(4, StickerType.MOOD, "${MOOD_PREFIX}4"),
    JOY(5, StickerType.MOOD, "${MOOD_PREFIX}5"),
    SADNESS(6, StickerType.MOOD, "${MOOD_PREFIX}6"),
    PANIC(7, StickerType.MOOD, "${MOOD_PREFIX}7"),
    SLEEPINESS(8, StickerType.MOOD, "${MOOD_PREFIX}8"),
    ANGER(9, StickerType.MOOD, "${MOOD_PREFIX}9"),
    DEPRESSION(10, StickerType.MOOD, "${MOOD_PREFIX}10"),
    UNPLEASANT(11, StickerType.MOOD, "${MOOD_PREFIX}11"),
    IMPASSIVE(12, StickerType.MOOD, "${MOOD_PREFIX}12"),
    SICK(13, StickerType.MOOD, "${MOOD_PREFIX}13"),
    CONFUSION(14, StickerType.MOOD, "${MOOD_PREFIX}14"),
    TIRED(15, StickerType.MOOD, "${MOOD_PREFIX}15"),
    SUNNY(16, StickerType.DAILY, "${DAILY_PREFIX}1"),
    RAINY(17, StickerType.DAILY, "${DAILY_PREFIX}2"),
    CLOUDY(18, StickerType.DAILY, "${DAILY_PREFIX}3"),
    COFFEE(19, StickerType.DAILY, "${DAILY_PREFIX}4"),
    MEAL(20, StickerType.DAILY, "${DAILY_PREFIX}5"),
    BEER(21, StickerType.DAILY, "${DAILY_PREFIX}6"),
    DESSERT(22, StickerType.DAILY, "${DAILY_PREFIX}7"),
    AVOCADO(23, StickerType.DAILY, "${DAILY_PREFIX}8"),
    BIRTHDAY_CAKE(24, StickerType.DAILY, "${DAILY_PREFIX}9"),
    WORK(25, StickerType.DAILY, "${DAILY_PREFIX}10"),
    MOVIE(26, StickerType.DAILY, "${DAILY_PREFIX}11"),
    NETFLIX(27, StickerType.DAILY, "${DAILY_PREFIX}12"),
    HOME(28, StickerType.DAILY, "${DAILY_PREFIX}13"),
    MEDICINE(29, StickerType.DAILY, "${DAILY_PREFIX}14"),
    PLANT(30, StickerType.DAILY, "${DAILY_PREFIX}15");

    companion object {
        fun fromBackupValue(backupValue: String) = values().first { it.backupValue == backupValue }
    }
}

enum class StickerType(val text: String) {
    MOOD("기분"),
    DAILY("일상"),
}

val Int.sticker: Sticker
    get() = Sticker.values().first { it.value == this }

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
        Sticker.SUNNY -> R.drawable.sunny
        Sticker.RAINY -> R.drawable.rainy
        Sticker.CLOUDY -> R.drawable.cloudy
        Sticker.COFFEE -> R.drawable.coffee
        Sticker.MEAL -> R.drawable.meal
        Sticker.BEER -> R.drawable.beer
        Sticker.DESSERT -> R.drawable.dessert
        Sticker.AVOCADO -> R.drawable.avocado
        Sticker.BIRTHDAY_CAKE -> R.drawable.birthday_cake
        Sticker.WORK -> R.drawable.work
        Sticker.MOVIE -> R.drawable.movie
        Sticker.NETFLIX -> R.drawable.netflix
        Sticker.HOME -> R.drawable.home
        Sticker.MEDICINE -> R.drawable.medicine
        Sticker.PLANT -> R.drawable.plant
    }