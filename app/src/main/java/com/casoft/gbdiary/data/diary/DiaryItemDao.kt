package com.casoft.gbdiary.data.diary

import androidx.room.*

@Dao
interface DiaryItemDao {

    @Query("SELECT * FROM DiaryItem WHERE isSync = 0")
    fun getNotSyncedDiaryItems(): List<DiaryItemEntity>

    @Insert
    fun insert(item: DiaryItemEntity)

    @Insert
    fun insertAll(items: List<DiaryItemEntity>)

    @Update
    fun update(item: DiaryItemEntity)

    @Delete
    fun delete(item: DiaryItemEntity)

    @Query("DELETE FROM DiaryItem")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        deleteAll()
        insertAll(items)
    }
}