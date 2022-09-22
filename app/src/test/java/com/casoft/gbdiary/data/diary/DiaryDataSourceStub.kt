package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.toDiaryItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.YearMonth

class DiaryDataSourceStub : DiaryDataSource {

    private val _diaryItems = mutableListOf<DiaryItemEntity>()
    val diaryItems
        get() = _diaryItems.toList()

    override fun getDiaryItemsByYearMonth(
        yearMonth: YearMonth
    ): Flow<List<DiaryItemEntity>> = flow {
        diaryItems.filter { YearMonth.of(it.date.year, it.date.month) == yearMonth }
    }

    override fun getDiaryItemsByDate(date: LocalDate): Flow<DiaryItemEntity> = flow {
        diaryItems.first { it.date.toLocalDate() == date }
    }

    override fun getAllDiaryItems(): List<DiaryItemEntity> = diaryItems

    override suspend fun save(item: DiaryItem) {
        delete(item)
        _diaryItems.add(item.toDiaryItemEntity())
    }

    override suspend fun delete(item: DiaryItem) {
        _diaryItems.removeAll { it.date.toLocalDate() == item.date }
    }

    override fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        _diaryItems.run {
            clear()
            addAll(items)
        }
    }

    fun setupTestData() {
        _diaryItems.addAll(
            listOf(
                DiaryItemEntity(
                    date = Date(year = 2022, month = 7, dayOfMonth = 31),
                    stickers = listOf(
                        Sticker.ANGER,
                        Sticker.CONFUSION,
                        Sticker.DEPRESSION
                    ),
                    contents = "22년 7월 31일"
                ),
                DiaryItemEntity(
                    date = Date(year = 2022, month = 8, dayOfMonth = 11),
                    stickers = listOf(Sticker.HOPEFUL),
                    contents = "22년 8월 1일"
                )
            )
        )
    }
}