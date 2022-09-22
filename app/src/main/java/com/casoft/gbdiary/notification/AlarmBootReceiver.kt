package com.casoft.gbdiary.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.ApplicationScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@AndroidEntryPoint
class AlarmBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var settingsDataSource: SettingsDataSource

    @Inject
    lateinit var diaryAlarmManager: DiaryAlarmManager

    @ApplicationScope
    @Inject
    lateinit var externalScope: CoroutineScope

    private val notificationTime = settingsDataSource.getNotificationTime()
        .stateIn(
            scope = externalScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val time = notificationTime.value
            if (time != null) {
                diaryAlarmManager.setAlarm(time)
            }
        }
    }
}