package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.toDiaryItem
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject

class GetDiaryItemUseCase @Inject constructor(
    private val diaryDataSource: DiaryDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<LocalDate, DiaryItem?>(ioDispatcher) {

    override fun execute(params: LocalDate): Flow<Result<DiaryItem?>> {
        return diaryDataSource.getDiaryItemsByDate(params)
            .map { Result.Success(it?.toDiaryItem()) }
    }
}