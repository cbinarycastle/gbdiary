package com.casoft.gbdiary.util

import android.app.AlarmManager
import android.content.Context
import android.os.Build

fun Context.canScheduleExactAlarm(): Boolean {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
    } else {
        true
    }
}