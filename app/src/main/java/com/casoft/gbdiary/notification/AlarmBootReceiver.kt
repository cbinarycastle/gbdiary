package com.casoft.gbdiary.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.ApplicationScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsDataSource: SettingsDataSource

    @Inject
    lateinit var notificationAlarmManager: NotificationAlarmManager

    @ApplicationScope
    @Inject
    lateinit var externalScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            externalScope.launch {
                val notificationTime = settingsDataSource.getNotificationTime().firstOrNull()
                if (notificationTime != null) {
                    notificationAlarmManager.setAlarm(notificationTime)
                }
            }
        }
    }
}