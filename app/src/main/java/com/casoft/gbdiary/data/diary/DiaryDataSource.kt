package com.casoft.gbdiary.data.diary

interface DiaryDataSource {

    fun getNotSyncedDiaryItems(): List<DiaryItemEntity>

    fun deleteAllAndInsertAll(items: List<DiaryItemEntity>)
}