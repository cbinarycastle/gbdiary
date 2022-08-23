package com.casoft.gbdiary.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager: AlarmManager? = context.getSystemService()

    fun setAlarm(time: LocalTime) {
        val pendingIntent = makePendingIntent()
        val triggerAtMillis = LocalDate.now()
            .atTime(time)
            .toEpochSecond(ZoneOffset.UTC)

        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelAlarm() {
        val pendingIntent = makePendingIntent()
        alarmManager?.cancel(pendingIntent)
    }

    private fun makePendingIntent(): PendingIntent {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getBroadcast(context, 0, intent, flags)
    }
}