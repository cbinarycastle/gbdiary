package com.casoft.gbdiary.data.diary

import kotlinx.coroutines.flow.Flow
import org.threeten.bp.YearMonth

interface DiaryDataSource {

    fun getDiaryItemsByYearMonth(yearMonth: YearMonth): Flow<List<DiaryItemEntity>>

    fun getNotSyncedDiaryItems(): List<DiaryItemEntity>

    fun deleteAllAndInsertAll(items: List<DiaryItemEntity>)
}