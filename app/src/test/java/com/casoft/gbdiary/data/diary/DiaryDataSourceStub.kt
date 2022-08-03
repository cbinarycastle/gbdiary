package com.casoft.gbdiary.data.diary

import org.threeten.bp.LocalDate

class DiaryDataSourceStub : DiaryDataSource {

    private val _diaryItems = mutableListOf<DiaryItemEntity>()
    val diaryItems
        get() = _diaryItems.toList()

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
                    day = LocalDate.of(2022, 7, 31),
                    sticker = listOf(1, 2, 4),
                    contents = "22년 7월 31일"
                ),
                DiaryItemEntity(
                    day = LocalDate.of(2022, 8, 11),
                    sticker = listOf(5),
                    contents = "22년 8월 1일"
                )
            )
        )
    }
}