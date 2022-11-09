package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveBiometricsSettingUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, Boolean>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<Boolean>> {
        return settingsDataSource.isBiometricsEnabled()
            .map { Result.Success(it) }
    }
}