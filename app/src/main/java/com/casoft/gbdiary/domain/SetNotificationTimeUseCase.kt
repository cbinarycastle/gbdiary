package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.notification.DiaryAlarmManager
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class SetNotificationTimeUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    private val diaryAlarmManager: DiaryAlarmManager,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<LocalTime, Unit>(ioDispatcher) {

    override suspend fun execute(params: LocalTime) {
        settingsDataSource.setNotificationTime(time = params)

        val dateTime = LocalDate.now().atTime(params).atZone(ZoneId.systemDefault())
        diaryAlarmManager.setAlarm(dateTime = dateTime)
    }
}