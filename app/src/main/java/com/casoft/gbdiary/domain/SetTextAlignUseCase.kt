package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.TextAlign
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SetTextAlignUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<TextAlign, Unit>(ioDispatcher) {

    override suspend fun execute(params: TextAlign) {
        settingsDataSource.setTextAlign(params)
    }
}