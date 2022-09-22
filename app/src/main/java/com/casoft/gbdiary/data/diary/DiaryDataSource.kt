package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.DiaryItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface DiaryDataSource {

    fun getDiaryItemsByYearMonth(yearMonth: YearMonth): Flow<List<DiaryItemEntity>>

    fun getDiaryItemsByDate(date: LocalDate): Flow<DiaryItemEntity?>

    fun getAllDiaryItems(): List<DiaryItemEntity>

    suspend fun save(item: DiaryItem)

    suspend fun delete(item: DiaryItem)

    fun deleteAllAndInsertAll(items: List<DiaryItemEntity>)
}