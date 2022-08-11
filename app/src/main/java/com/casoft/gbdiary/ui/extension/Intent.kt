package com.casoft.gbdiary.ui.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.navigateToAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply { data = Uri.parse("package:${packageName}") }
    startActivity(intent)
}
