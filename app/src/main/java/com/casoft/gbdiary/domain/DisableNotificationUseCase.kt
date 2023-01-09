package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.notification.NotificationAlarmManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DisableNotificationUseCase @Inject constructor(
    private val notificationAlarmManager: NotificationAlarmManager,
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Unit, Unit>(ioDispatcher) {

    override suspend fun execute(params: Unit) {
        notificationAlarmManager.cancelAlarm()
        settingsDataSource.setNotificationTime(null)
        notificationAlarmManager.disableAlarmBootReceiver()
    }
}