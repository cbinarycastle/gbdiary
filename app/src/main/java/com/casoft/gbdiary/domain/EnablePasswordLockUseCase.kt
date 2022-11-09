package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class EnablePasswordLockUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<String, Unit>(ioDispatcher) {

    override suspend fun execute(params: String) {
        settingsDataSource.setPassword(password = params)
    }
}