package com.casoft.gbdiary.model

import com.casoft.gbdiary.R

enum class Sticker(val value: Int, val type: StickerType) {

    SMILE(1, StickerType.MOOD),
    HAPPINESS(2, StickerType.MOOD),
    HOPEFUL(3, StickerType.MOOD),
    SATISFACTION(4, StickerType.MOOD),
    JOY(5, StickerType.MOOD),
    SADNESS(6, StickerType.MOOD),
    PANIC(7, StickerType.MOOD),
    SLEEPINESS(8, StickerType.MOOD),
    ANGER(9, StickerType.MOOD),
    DEPRESSION(10, StickerType.MOOD),
    UNPLEASANT(11, StickerType.MOOD),
    IMPASSIVE(12, StickerType.MOOD),
    SICK(13, StickerType.MOOD),
    CONFUSION(14, StickerType.MOOD),
    TIRED(15, StickerType.MOOD),
    BEER(16, StickerType.DAILY),
    COFFEE(17, StickerType.DAILY),
    MEAL(18, StickerType.DAILY),
    SUNNY(19, StickerType.DAILY),
    AVOCADO(20, StickerType.DAILY),
    WORK(21, StickerType.DAILY),
    RAINY(22, StickerType.DAILY),
    HOME(23, StickerType.DAILY),
    NETFLIX(24, StickerType.DAILY),
    DESSERT(25, StickerType.DAILY),
    BIRTHDAY_CAKE(26, StickerType.DAILY),
    MOVIE(27, StickerType.DAILY),
    MEDICINE(28, StickerType.DAILY),
    CLOUDY(29, StickerType.DAILY),
    PLANT(30, StickerType.DAILY);
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
        Sticker.BEER -> R.drawable.beer
        Sticker.COFFEE -> R.drawable.coffee
        Sticker.MEAL -> R.drawable.meal
        Sticker.SUNNY -> R.drawable.sunny
        Sticker.AVOCADO -> R.drawable.avocado
        Sticker.WORK -> R.drawable.work
        Sticker.RAINY -> R.drawable.rainy
        Sticker.HOME -> R.drawable.home
        Sticker.NETFLIX -> R.drawable.netflix
        Sticker.DESSERT -> R.drawable.dessert
        Sticker.BIRTHDAY_CAKE -> R.drawable.birthday_cake
        Sticker.MOVIE -> R.drawable.movie
        Sticker.MEDICINE -> R.drawable.medicine
        Sticker.CLOUDY -> R.drawable.cloudy
        Sticker.PLANT -> R.drawable.plant
    }