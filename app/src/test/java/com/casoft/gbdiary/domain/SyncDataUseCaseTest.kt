package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupDataSourceStub
import com.casoft.gbdiary.data.diary.DiaryDataSourceStub
import com.casoft.gbdiary.data.diary.DiaryImageDataSourceStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SyncDataUseCaseTest {

    @Test
    fun `데이터 복원이 정상적으로 완료된다`() = runTest {
        val backupDataSource = BackupDataSourceStub().apply { setupTestData() }
        val diaryDataSource = DiaryDataSourceStub()
        val diaryImageDataSource = DiaryImageDataSourceStub()

        val syncDataUseCase = SyncDataUseCase(
            backupDataSource = backupDataSource,
            diaryDataSource = diaryDataSource,
            diaryImageDataSource = diaryImageDataSource,
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val account = Mockito.mock(Account::class.java)
        syncDataUseCase(account)

        assertEquals(expected = 2, actual = diaryDataSource.diaryItems.size)
        assertEquals(expected = 3, actual = diaryImageDataSource.files.size)
    }
}