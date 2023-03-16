package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Statistics
import kotlinx.coroutines.CoroutineDispatcher
import java.time.YearMonth
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val diaryDataSource: DiaryDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<YearMonth, Statistics>(ioDispatcher) {

    override suspend fun execute(params: YearMonth): Statistics {
        val stickerCounts = diaryDataSource.getDiaryItemsByYearMonth(yearMonth = params)
            .flatMap { it.stickers }
            .groupBy { it }
            .map { (sticker, stickers) -> Statistics.StickerCount(sticker, stickers.size) }
            .sortedWith(
                compareByDescending<Statistics.StickerCount> { it.count }
                    .thenBy { it.sticker.name }
            )

        return Statistics(yearMonth = params, stickerCounts = stickerCounts)
    }
}