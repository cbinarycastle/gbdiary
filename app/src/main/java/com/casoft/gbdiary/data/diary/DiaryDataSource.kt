package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.DiaryItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface DiaryDataSource {

    fun getDiaryItemsByYearMonth(yearMonth: YearMonth): Flow<List<DiaryItemEntity>>

    fun getDiaryItemsByDate(date: LocalDate): Flow<DiaryItemEntity?>

    fun getNotSyncedDiaryItems(): List<DiaryItemEntity>

    suspend fun save(item: DiaryItem)

    suspend fun updateSyncAll(isSync: Boolean)

    suspend fun deleteDiaryitem(item: DiaryItem)

    fun deleteAllAndInsertAll(items: List<DiaryItemEntity>)
}