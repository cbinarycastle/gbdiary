package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupData
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.IMAGE_FILE_EXTENSION
import com.casoft.gbdiary.data.diary.toDiaryItem
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.toBackupData
import com.google.api.services.drive.model.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class BackupDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Account, BackupResult>(ioDispatcher) {

    override fun execute(params: Account): Flow<Result<BackupResult>> = channelFlow {
        val diaryItemsToBackup = diaryDataSource.getNotSyncedDiaryItems()
        if (diaryItemsToBackup.isEmpty()) {
            send(Result.Success(BackupResult.ALREADY_COMPLETED))
            return@channelFlow
        }

        send(Result.Loading.Start)
        delay(200)

        val progressUnit = 0.95f / diaryItemsToBackup.size
        val finishedJobs = AtomicInteger()

        val existingBackupDataItems = backupDataSource.getData(account = params)
            .data
            .toMutableList()

        coroutineScope {
            diaryItemsToBackup.map { diaryItemEntity ->
                async {
                    val diaryItem = diaryItemEntity.toDiaryItem()
                    val uploadedImageIds = uploadImages(
                        account = params,
                        date = diaryItem.date,
                        images = diaryItem.images
                    ).map { it.id }

                    val backupDataItem = diaryItem.toBackupData(imageIds = uploadedImageIds)
                    val existingBackupDataItemIndex = existingBackupDataItems.indexOfFirst {
                        it.day == backupDataItem.day
                    }

                    if (existingBackupDataItemIndex >= 0) {
                        existingBackupDataItems[existingBackupDataItemIndex] = backupDataItem
                    } else {
                        existingBackupDataItems.add(backupDataItem)
                    }

                    val progress = progressUnit * finishedJobs.incrementAndGet()
                    send(Result.Loading(progress))
                }
            }.awaitAll()

            backupDataSource.deleteData(account = params)
            backupDataSource.uploadData(
                account = params,
                backupData = BackupData(existingBackupDataItems)
            )

            diaryDataSource.updateSyncAll(true)

            send(Result.Loading.End)
            delay(200)
            send(Result.Success(BackupResult.COMPLETED))
        }
    }

    private suspend fun uploadImages(
        account: Account,
        date: LocalDate,
        images: List<String>,
    ): List<File> {
        return images.mapIndexed { index, filePath ->
            val backupFileName = "${date}_${index + 1}.$IMAGE_FILE_EXTENSION"
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
    ALREADY_COMPLETED, COMPLETED
}