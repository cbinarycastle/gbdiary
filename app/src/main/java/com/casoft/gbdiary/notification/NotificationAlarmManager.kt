package com.casoft.gbdiary.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.getSystemService
import com.casoft.gbdiary.time.TimeProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val timeProvider: TimeProvider,
) {
    private val alarmManager: AlarmManager? = context.getSystemService()

    fun setAlarm(time: LocalTime) {
        val pendingIntent = makePendingIntent()

        val now = timeProvider.now()
        val date = if (time <= now.toLocalTime()) {
            now.toLocalDate().plusDays(1)
        } else {
            now.toLocalDate()
        }

        val triggerAtMillis = date.atTime(time)
            .atZone(timeProvider.zone())
            .toInstant()
            .toEpochMilli()

        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun enableAlarmBootReceiver() {
        val receiver = ComponentName(context, AlarmBootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun cancelAlarm() {
        val pendingIntent = makePendingIntent()
        alarmManager?.cancel(pendingIntent)
    }

    fun disableAlarmBootReceiver() {
        val receiver = ComponentName(context, AlarmBootReceiver::class.java)

        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun makePendingIntent(): PendingIntent {
        val intent = Intent(context, DiaryAlarmBroadcastReceiver::class.java)
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val requestCode = System.currentTimeMillis().toInt()
        return PendingIntent.getBroadcast(context, requestCode, intent, flags)
    }
}