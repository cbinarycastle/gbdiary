package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.toDiaryItem
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchDiaryItemsUseCase @Inject constructor(
    private val diaryDataSource: DiaryDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<String, List<DiaryItem>>(ioDispatcher) {

    override fun execute(params: String): Flow<Result<List<DiaryItem>>> = flow {
        emit(Result.Loading())

        diaryDataSource.loadDiaryItemsByContents(params)
            .map { items ->
                items.map { item -> item.toDiaryItem() }
                    .let { Result.Success(it) }
            }
            .let { emitAll(it) }
    }
}