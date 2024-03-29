package com.casoft.gbdiary.data.diary

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryItemDao {

    @Query("SELECT * FROM DiaryItem")
    suspend fun getAll(): List<DiaryItemEntity>

    @Query("SELECT COUNT(*) FROM DiaryItem")
    fun countStream(): Flow<Int>

    @Query("SELECT * FROM DiaryItem WHERE year = :year AND month = :month")
    fun getStreamByYearAndMonth(year: Int, month: Int): Flow<List<DiaryItemEntity>>

    @Query("SELECT * FROM DiaryItem WHERE year = :year AND month = :month")
    suspend fun getByYearAndMonth(year: Int, month: Int): List<DiaryItemEntity>

    @Query("SELECT * FROM DiaryItem WHERE year = :year AND month = :month AND dayOfMonth = :dayOfMonth")
    fun getStreamByDate(year: Int, month: Int, dayOfMonth: Int): Flow<DiaryItemEntity?>

    @Query("SELECT * FROM DiaryItem WHERE contents LIKE :contents")
    fun findStreamByContents(contents: String): Flow<List<DiaryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: DiaryItemEntity)

    @Insert
    suspend fun insertAll(items: List<DiaryItemEntity>)

    @Delete
    suspend fun delete(item: DiaryItemEntity)

    @Query("DELETE FROM DiaryItem")
    suspend fun deleteAll()

    @Transaction
    suspend fun deleteAllAndInsertAll(items: List<DiaryItemEntity>) {
        deleteAll()
        insertAll(items)
    }
}