package com.casoft.gbdiary.data.diary

import kotlinx.coroutines.flow.Flow
import org.threeten.bp.YearMonth

class DefaultDiaryDataSource(private val diaryItemDao: DiaryItemDao): DiaryDataSource {

    override fun getDiaryItemsByYearMonth(yearMonth: YearMonth): Flow<List<DiaryItemEntity>> {
        return diaryItemDao.getStreamByYearAndMonth(
            year = yearMonth.year,
            month = yearMonth.monthValue
        )
    }

    override fun getNotSyncedDiaryItems(): List<DiaryItemEntity> {
        return diaryItemDao.getNotSynced()
    }

    override fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        return diaryItemDao.deleteAllAndInsertAll(items)
    }
}