package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalTime
import javax.inject.Inject

class SetNotificationTimeUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<LocalTime, Unit>(ioDispatcher) {

    override suspend fun execute(params: LocalTime) {
        settingsDataSource.setNotificationTime(time = params)
    }
}