package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.DiaryItem
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteDiaryItemUseCase @Inject constructor(
    private val diaryDataSource: DiaryDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<DiaryItem, Unit>(ioDispatcher) {

    override suspend fun execute(params: DiaryItem) {
        diaryDataSource.delete(params)
    }
}