package com.casoft.gbdiary.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.casoft.gbdiary.R
import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.ApplicationScope
import com.casoft.gbdiary.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DiaryAlarmBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsDataSource: SettingsDataSource

    @Inject
    lateinit var notificationAlarmManager: NotificationAlarmManager

    @ApplicationScope
    @Inject
    lateinit var externalScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingIntent = makePendingIntent(context)
        val notification = NotificationCompat.Builder(context, GBDiaryNotificationChannel.DIARY.id)
            .setSmallIcon(R.drawable.satisfaction)
            .setContentText("오늘도 꼬박꼬박 하루를 기록해보세요 :)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(createNotificationId(), notification)
        }

        externalScope.launch {
            val notificationTime = settingsDataSource.getNotificationTime().firstOrNull()
            if (notificationTime != null) {
                notificationAlarmManager.setAlarm(notificationTime)
            }
        }
    }

    private fun makePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    private fun createNotificationId(): Int {
        return DateTimeFormatter.ofPattern("dHHmmss", Locale.KOREA)
            .format(LocalDateTime.now())
            .toInt()
    }
}