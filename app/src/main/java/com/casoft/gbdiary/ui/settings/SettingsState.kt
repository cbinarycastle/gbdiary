package com.casoft.gbdiary.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberSettingsState(
    context: Context = LocalContext.current,
) = remember(context) { SettingsState(context) }

@Immutable
class SettingsState(private val context: Context) {

    fun navigateToGooglePlay() {
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = uri
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW).apply { data = uri }
            context.startActivity(intent)
        }
    }
}