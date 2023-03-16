package com.casoft.gbdiary.util

import androidx.compose.ui.graphics.Color
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.Sticker

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

val Sticker.color: Color
    get() = when (this) {
        Sticker.SMILE -> Color(0xFFFFF04F)
        Sticker.HAPPINESS -> Color(0xFFFFC766)
        Sticker.HOPEFUL -> Color(0xFFFFC6CD)
        Sticker.SATISFACTION -> Color(0xFFBBF58D)
        Sticker.JOY -> Color(0xFFFFCEF5)
        Sticker.SADNESS -> Color(0xFF95DEFF)
        Sticker.PANIC -> Color(0xFFDBD0FA)
        Sticker.SLEEPINESS -> Color(0xFFA8D4FF)
        Sticker.ANGER -> Color(0xFFFF764E)
        Sticker.DEPRESSION -> Color(0xFF7FC19A)
        Sticker.UNPLEASANT -> Color(0xFF98EBF5)
        Sticker.IMPASSIVE -> Color(0xFFBAF4D1)
        Sticker.SICK -> Color(0xFFE8D3C1)
        Sticker.CONFUSION -> Color(0xFF98A9DF)
        Sticker.TIRED -> Color(0xFFA7A7A8)
        Sticker.SUNNY -> Color(0xFFFFC636)
        Sticker.RAINY -> Color(0xFFA4E4E4)
        Sticker.CLOUDY -> Color(0xFFC8E2EE)
        Sticker.COFFEE -> Color(0xFFFED5CC)
        Sticker.MEAL -> Color(0xFFDDDDDD)
        Sticker.BEER -> Color(0xFFFDCE30)
        Sticker.DESSERT -> Color(0xFFFFE276)
        Sticker.AVOCADO -> Color(0xFFBCE5A2)
        Sticker.BIRTHDAY_CAKE -> Color(0xFFFFCCE1)
        Sticker.WORK -> Color(0xFFD9D9D9)
        Sticker.MOVIE -> Color(0xFFB6D6FF)
        Sticker.NETFLIX -> Color(0xFF4B4B4B)
        Sticker.HOME -> Color(0xFFFFC9BE)
        Sticker.MEDICINE -> Color(0xFFFFE35B)
        Sticker.PLANT -> Color(0xFF91BD89)
    }