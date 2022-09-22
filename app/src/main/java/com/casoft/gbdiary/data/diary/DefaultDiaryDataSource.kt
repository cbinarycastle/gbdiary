package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.toDiaryItemEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

class DefaultDiaryDataSource(private val diaryItemDao: DiaryItemDao) : DiaryDataSource {

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

    override fun getAllDiaryItems(): List<DiaryItemEntity> {
        return diaryItemDao.getAll()
    }

    override suspend fun save(item: DiaryItem) {
        diaryItemDao.insertOrUpdate(item.toDiaryItemEntity())
    }

    override suspend fun delete(item: DiaryItem) {
        diaryItemDao.delete(item.toDiaryItemEntity())
    }

    override fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        return diaryItemDao.deleteAllAndInsertAll(items)
    }
}