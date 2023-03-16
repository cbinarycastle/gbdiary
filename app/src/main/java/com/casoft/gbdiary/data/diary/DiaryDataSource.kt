package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.DiaryItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface DiaryDataSource {

    fun loadDiaryItemsByYearMonth(yearMonth: YearMonth): Flow<List<DiaryItemEntity>>

    suspend fun getDiaryItemsByYearMonth(yearMonth: YearMonth): List<DiaryItemEntity>

    fun loadDiaryItemByDate(date: LocalDate): Flow<DiaryItemEntity?>

    fun loadDiaryItemsByContents(contents: String): Flow<List<DiaryItemEntity>>

    suspend fun getAllDiaryItems(): List<DiaryItemEntity>

    suspend fun save(item: DiaryItem)

    suspend fun delete(item: DiaryItem)

    suspend fun deleteAllAndInsertAll(items: List<DiaryItemEntity>)
}