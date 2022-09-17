package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.toDiaryItemEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

class DefaultDiaryDataSource(private val diaryItemDao: DiaryItemDao) : DiaryDataSource {

    override fun getDiaryItemsByYearMonth(yearMonth: YearMonth): Flow<List<DiaryItemEntity>> {
        return diaryItemDao.getEnabledStreamByYearAndMonth(
            year = yearMonth.year,
            month = yearMonth.monthValue
        )
    }

    override fun getDiaryItemsByDate(date: LocalDate): Flow<DiaryItemEntity> {
        return diaryItemDao.getEnabledStreamByDate(
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

    override suspend fun updateSyncAll(isSync: Boolean) {
        diaryItemDao.updateSyncAll(isSync)
    }

    override suspend fun deleteDiaryitem(item: DiaryItem) {
        val itemEntity = item.toDiaryItemEntity()
            .copy(
                status = DiaryItemStatus.DELETED,
                isSync = false
            )
        diaryItemDao.update(itemEntity)
    }

    override fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        return diaryItemDao.deleteAllAndInsertAll(items)
    }
}