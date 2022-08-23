package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.notification.DiaryAlarmManager
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalTime
import javax.inject.Inject

class EnableNotificationUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val diaryAlarmManager: DiaryAlarmManager,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Unit, Unit>(ioDispatcher) {

    override suspend fun execute(params: Unit) {
        val time = LocalTime.of(22, 0)
        diaryAlarmManager.setAlarm(time)
        settingsDataSource.setNotificationTime(time)
    }
}