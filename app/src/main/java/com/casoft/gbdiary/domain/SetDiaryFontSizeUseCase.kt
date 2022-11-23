package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.settings.SettingsDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.DiaryFontSize
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SetDiaryFontSizeUseCase @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<DiaryFontSize, Unit>(ioDispatcher) {

    override suspend fun execute(params: DiaryFontSize) {
        settingsDataSource.setDiaryFontSize(diaryFontSize = params)
    }
}