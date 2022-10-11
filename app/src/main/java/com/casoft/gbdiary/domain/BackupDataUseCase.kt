package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupData
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.DiaryItemEntity
import com.casoft.gbdiary.data.diary.toDiaryItem
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.toBackupDataItem
import com.google.api.services.drive.model.File
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

class BackupDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Account, BackupResult>(ioDispatcher) {

    override fun execute(params: Account): Flow<Result<BackupResult>> = channelFlow {
        val diaryItems = diaryDataSource.getAllDiaryItems()
        if (diaryItems.isEmpty()) {
            send(Result.Success(BackupResult.NO_DATA))
            return@channelFlow
        }

        send(Result.Loading.Start)
        delay(100)

        val remoteFiles = backupDataSource.getAllFiles(account = params)

        val progress = JobProgress(
            numberOfJobs = diaryItems.size + remoteFiles.size,
            maxProgress = 0.99f
        )

        deleteAllFiles(
            account = params,
            files = remoteFiles,
            progress = progress
        )

        uploadBackupData(
            account = params,
            diaryItems = diaryItems,
            progress = progress
        )

        backupDataSource.setLatestBackupDateTime(ZonedDateTime.now())

        send(Result.Loading.End)
        delay(100)
        send(Result.Success(BackupResult.COMPLETED))
    }

    private suspend fun ProducerScope<Result<BackupResult>>.deleteAllFiles(
        account: Account,
        files: List<File>,
        progress: JobProgress,
    ) = coroutineScope {
        files.map {
            async {
                backupDataSource.deleteFile(account = account, fileId = it.id)
                val currentProgress = progress.increment()
                send(Result.Loading(currentProgress))
            }
        }.awaitAll()
    }

    private suspend fun ProducerScope<Result<BackupResult>>.uploadBackupData(
        account: Account,
        diaryItems: List<DiaryItemEntity>,
        progress: JobProgress,
    ) = coroutineScope {
        val backupDataItems = diaryItems.map { diaryItemEntity ->
            async {
                val diaryItem = diaryItemEntity.toDiaryItem()

                val uploadedImageIds = uploadImages(
                    account = account,
                    date = diaryItem.date,
                    images = diaryItem.images
                ).map { it.id }

                val currentProgress = progress.increment()
                send(Result.Loading(currentProgress))

                diaryItem.toBackupDataItem(uploadedImageIds)
            }
        }.awaitAll()

        backupDataSource.uploadData(
            account = account,
            backupData = BackupData(backupDataItems)
        )
    }

    private suspend fun uploadImages(
        account: Account,
        date: LocalDate,
        images: List<String>,
    ): List<File> {
        return images.mapIndexed { index, filePath ->
            val backupFileName = "${date}_${index + 1}"
            backupDataSource.deleteImage(account = account, fileName = backupFileName)
            backupDataSource.uploadImage(
                account = account,
                fileName = backupFileName,
                filePath = filePath
            )
        }
    }
}

enum class BackupResult {
    NO_DATA, COMPLETED
}