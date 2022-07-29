package com.casoft.gbdiary.data.diary

internal const val IMAGE_FILE_EXTENSION = "jpg"

class DefaultDiaryDataSource(private val diaryItemDao: DiaryItemDao): DiaryDataSource {

    override fun getNotSyncedDiaryItems(): List<DiaryItemEntity> =
        diaryItemDao.getNotSyncedDiaryItems()

    override fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) =
        diaryItemDao.deleteAllAndInsertAll(items)
}