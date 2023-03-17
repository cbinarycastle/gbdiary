package com.casoft.gbdiary.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

fun Context.navigateToAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply { data = Uri.parse("package:${packageName}") }
    startActivity(intent)
}

@RequiresApi(Build.VERSION_CODES.S)
fun Context.requestScheduleExactAlarm() {
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    startActivity(intent)
}

fun Context.navigateToGooglePlay() {
    val uri = Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")

    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = uri
            setPackage("com.android.vending")
        }
        startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW).apply { data = uri }
        startActivity(intent)
    }
}