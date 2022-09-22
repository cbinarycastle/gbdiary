package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupDataItem
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.toDiaryItemEntity
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.ImageDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    private val imageDataSource: ImageDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Account, Unit>(ioDispatcher) {

    override fun execute(params: Account): Flow<Result<Unit>> = channelFlow {
        send(Result.Loading.Start)
        delay(100)

        val backupData = backupDataSource.getData(account = params)
        val backupDataItems = backupData.data

        val progress = JobProgress(
            numberOfJobs = backupDataItems.size,
            maxProgress = 0.99f
        )

        syncData(
            account = params,
            backupDataItems = backupDataItems,
            progress = progress
        )

        send(Result.Loading.End)
        delay(100)
        send(Result.Success(Unit))
    }

    private suspend fun ProducerScope<Result<Unit>>.syncData(
        account: Account,
        backupDataItems: List<BackupDataItem>,
        progress: JobProgress,
    ) = coroutineScope {
        val diaryItems = backupDataItems.map { backupDataItem ->
            async {
                val images = backupDataItem.images.map { fileId ->
                    backupDataSource.download(account, fileId)
                        .let { inputStream ->
                            imageDataSource.saveImage(source = inputStream)
                        }
                        .path
                }

                val diaryItem = backupDataItem.toDiaryItemEntity(images)

                val currentProgress = progress.increment()
                send(Result.Loading(currentProgress))

                diaryItem
            }
        }.awaitAll()

        diaryDataSource.deleteAllAndInsertAll(diaryItems)
    }
}