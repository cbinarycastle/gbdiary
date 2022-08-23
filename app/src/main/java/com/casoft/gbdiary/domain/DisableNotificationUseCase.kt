package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.notification.DiaryAlarmManager
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DisableNotificationUseCase @Inject constructor(
    private val diaryAlarmManager: DiaryAlarmManager,
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Unit, Unit>(ioDispatcher) {

    override suspend fun execute(params: Unit) {
        diaryAlarmManager.cancelAlarm()
        settingsDataSource.setNotificationTime(null)
    }
}