package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.toDiaryItemEntity
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.ImageDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.*
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
        delay(200)

        val backupData = backupDataSource.getData(account = params)
        val backupDataItems = backupData.data
        val progress = JobProgress(
            numberOfJobs = backupDataItems.size,
            maxProgress = 0.95f
        )

        coroutineScope {
            val diaryItems = backupDataItems
                .map { backupDataItem ->
                    async {
                        val images = backupDataItem.images.map { fileId ->
                            backupDataSource.download(account = params, fileId = fileId)
                                .let { inputStream ->
                                    imageDataSource.saveImage(source = inputStream)
                                }
                                .path
                        }

                        val diaryItem = backupDataItem.toDiaryItemEntity(images = images)

                        val currentProgress = progress.increment()
                        send(Result.Loading(currentProgress))

                        diaryItem
                    }
                }
                .awaitAll()

            diaryDataSource.deleteAllAndInsertAll(diaryItems)

            send(Result.Loading.End)
            delay(200)
            send(Result.Success(Unit))
        }
    }
}