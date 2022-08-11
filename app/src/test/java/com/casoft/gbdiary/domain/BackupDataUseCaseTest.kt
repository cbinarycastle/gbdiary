package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupDataSourceStub
import com.casoft.gbdiary.data.diary.DiaryDataSourceStub
import com.casoft.gbdiary.data.diary.ImageDataSourceStub
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class BackupDataUseCaseTest {

    @Test
    fun `데이터 백업이 정상적으로 완료된다`() = runTest {
        val backupDataSource = BackupDataSourceStub()
        val diaryDataSource = DiaryDataSourceStub().apply { setupTestData() }

        val backupDataUseCase = BackupDataUseCase(
            backupDataSource = backupDataSource,
            diaryDataSource = diaryDataSource,
            imageDataSource = ImageDataSourceStub(),
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val account = Mockito.mock(Account::class.java)
        backupDataUseCase(account)

        val backupData = backupDataSource.backupData
        assertNotNull(backupData)
        assertEquals(expected = 2, actual = backupData.data.size)
    }
}