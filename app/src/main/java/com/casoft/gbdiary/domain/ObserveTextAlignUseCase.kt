package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.TextAlign
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveTextAlignUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, TextAlign>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<TextAlign>> {
        return settingsDataSource.getTextAlign()
            .map { Result.Success(it) }
    }
}