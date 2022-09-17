package com.casoft.gbdiary.data.diary

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryItemDao {

    @Query("SELECT COUNT(*) FROM DiaryItem")
    fun countStream(): Flow<Int>

    @Query("SELECT * FROM DiaryItem WHERE year = :year AND month = :month AND status = 1")
    fun getEnabledStreamByYearAndMonth(year: Int, month: Int): Flow<List<DiaryItemEntity>>

    @Query("SELECT * FROM DiaryItem WHERE year = :year AND month = :month AND dayOfMonth = :dayOfMonth AND status = 1")
    fun getEnabledStreamByDate(year: Int, month: Int, dayOfMonth: Int): Flow<DiaryItemEntity>

    @Query("SELECT * FROM DiaryItem WHERE isSync = 0")
    fun getNotSynced(): List<DiaryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(item: DiaryItemEntity)

    @Insert
    fun insertAll(items: List<DiaryItemEntity>)

    @Update
    fun update(item: DiaryItemEntity)

    @Query("UPDATE DiaryItem SET isSync = :isSync WHERE isSync != :isSync")
    fun updateSyncAll(isSync: Boolean)

    @Query("DELETE FROM DiaryItem")
    fun deleteAll()

    @Transaction
    fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        deleteAll()
        insertAll(items)
    }
}