package com.casoft.gbdiary.model

import com.casoft.gbdiary.R

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
    BEER(StickerType.DAILY),
    COFFEE(StickerType.DAILY),
    MEAL(StickerType.DAILY),
    SUNNY(StickerType.DAILY),
    AVOCADO(StickerType.DAILY),
    WORK(StickerType.DAILY),
    RAINY(StickerType.DAILY),
    HOME(StickerType.DAILY),
    NETFLIX(StickerType.DAILY),
    DESSERT(StickerType.DAILY),
    BIRTHDAY_CAKE(StickerType.DAILY),
    MOVIE(StickerType.DAILY),
    MEDICINE(StickerType.DAILY),
    CLOUDY(StickerType.DAILY),
    PLANT(StickerType.DAILY),
}

enum class StickerType(val text: String) {
    MOOD("기분"),
    DAILY("일상"),
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