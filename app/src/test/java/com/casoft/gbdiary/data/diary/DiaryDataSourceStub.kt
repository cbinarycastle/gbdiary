package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.Sticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.threeten.bp.YearMonth

class DiaryDataSourceStub : DiaryDataSource {

    private val _diaryItems = mutableListOf<DiaryItemEntity>()
    val diaryItems
        get() = _diaryItems.toList()

    override fun getDiaryItemsByYearMonth(
        yearMonth: YearMonth
    ): Flow<List<DiaryItemEntity>> = flow {
        diaryItems.filter { YearMonth.of(it.date.year, it.date.month) == yearMonth }
    }

    override fun getNotSyncedDiaryItems(): List<DiaryItemEntity> {
        return diaryItems.filter { it.isSync.not() }
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
                    sticker = listOf(
                        Sticker.ANGER,
                        Sticker.CONFUSION,
                        Sticker.DEPRESSION
                    ),
                    contents = "22년 7월 31일"
                ),
                DiaryItemEntity(
                    date = Date(year = 2022, month = 8, dayOfMonth = 11),
                    sticker = listOf(Sticker.HOPEFUL),
                    contents = "22년 8월 1일"
                )
            )
        )
    }
}