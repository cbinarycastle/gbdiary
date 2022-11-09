package com.casoft.gbdiary.util

import android.content.Context
import android.content.Intent
import android.net.Uri

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