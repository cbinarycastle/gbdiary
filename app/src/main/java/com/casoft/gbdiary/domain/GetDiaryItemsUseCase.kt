package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.toDiaryItem
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

class GetDiaryItemsUseCase @Inject constructor(
    private val diaryDataSource: DiaryDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<YearMonth, List<DiaryItem>>(ioDispatcher) {

    override fun execute(params: YearMonth): Flow<Result<List<DiaryItem>>> {
        return diaryDataSource.getDiaryItemsByYearMonth(params)
            .map { items ->
                val diaryItem = items.map { item -> item.toDiaryItem() }
                Result.Success(diaryItem)
            }
    }
}