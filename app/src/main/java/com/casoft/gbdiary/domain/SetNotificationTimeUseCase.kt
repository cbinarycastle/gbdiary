package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.notification.NotificationAlarmManager
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalTime
import javax.inject.Inject

class SetNotificationTimeUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val notificationAlarmManager: NotificationAlarmManager,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<LocalTime, Unit>(ioDispatcher) {

    override suspend fun execute(params: LocalTime) {
        settingsDataSource.setNotificationTime(time = params)
        notificationAlarmManager.setAlarm(time = params)
    }
}