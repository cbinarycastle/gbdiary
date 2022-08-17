package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.toDiaryItemEntity
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

class DefaultDiaryDataSource(private val diaryItemDao: DiaryItemDao): DiaryDataSource {

    override fun getDiaryItemsByYearMonth(yearMonth: YearMonth): Flow<List<DiaryItemEntity>> {
        return diaryItemDao.getStreamByYearAndMonth(
            year = yearMonth.year,
            month = yearMonth.monthValue
        )
    }

    override fun getDiaryItemsByDate(date: LocalDate): Flow<DiaryItemEntity> {
        return diaryItemDao.getStreamByDate(
            year = date.year,
            month = date.monthValue,
            dayOfMonth = date.dayOfMonth
        )
    }

    override fun getNotSyncedDiaryItems(): List<DiaryItemEntity> {
        return diaryItemDao.getNotSynced()
    }

    override suspend fun save(item: DiaryItem) {
        diaryItemDao.insertOrUpdate(item.toDiaryItemEntity())
    }

    override suspend fun deleteDiaryitem(item: DiaryItem) {
        diaryItemDao.delete(item.toDiaryItemEntity())
    }

    override fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        return diaryItemDao.deleteAllAndInsertAll(items)
    }
}