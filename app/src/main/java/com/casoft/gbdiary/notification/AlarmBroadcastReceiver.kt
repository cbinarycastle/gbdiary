package com.casoft.gbdiary.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.MainActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingIntent = makePendingIntent(context)
        val notification = NotificationCompat.Builder(context, GBDiaryNotificationChannel.DIARY.id)
            .setSmallIcon(R.drawable.satisfaction)
            .setContentText("오늘도 꼬박꼬박 하루를 기록해보세요 :)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(createNotificationId(), notification)
        }
    }

    private fun makePendingIntent(context: Context): PendingIntent {
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(context, 0, mainActivityIntent, flags)
    }

    private fun createNotificationId(): Int {
        return DateTimeFormatter.ofPattern("dHHmmss", Locale.KOREA)
            .format(LocalDateTime.now())
            .toInt()
    }
}