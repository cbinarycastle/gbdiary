package com.casoft.gbdiary.util.initializers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import androidx.startup.Initializer
import com.casoft.gbdiary.notification.GBDiaryNotificationChannel

class NotificationChannelInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = GBDiaryNotificationChannel.values().map { channel ->
                val id = channel.id
                val name = context.getString(channel.nameResId)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                NotificationChannel(id, name, importance)
            }

            val notificationManager = context.getSystemService() as? NotificationManager
            notificationManager?.createNotificationChannels(channel)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf()
}