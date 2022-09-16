package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupData
import com.casoft.gbdiary.data.backup.BackupDataDateFormatter
import com.casoft.gbdiary.data.backup.BackupDataNotFoundException
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.DiaryItemStatus
import com.casoft.gbdiary.data.diary.toDiaryItem
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.toBackupDataItem
import com.google.api.services.drive.model.File
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.time.LocalDate
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

        val progress = JobProgress(
            numberOfJobs = diaryItemsToBackup.size,
            maxProgress = 0.95f
        )

        val existingBackupDataItems = try {
            backupDataSource.getData(account = params)
                .data
                .toMutableList()
        } catch (e: BackupDataNotFoundException) {
            mutableListOf()
        }

        coroutineScope {
            diaryItemsToBackup.map { diaryItemEntity ->
                async {
                    val diaryItem = diaryItemEntity.toDiaryItem()

                    when (diaryItemEntity.status) {
                        DiaryItemStatus.ENABLED -> {
                            val uploadedImageIds = uploadImages(
                                account = params,
                                date = diaryItem.date,
                                images = diaryItem.images
                            ).map { it.id }

                            val backupDataItem =
                                diaryItem.toBackupDataItem(imageIds = uploadedImageIds)
                            val existingBackupDataItemIndex = existingBackupDataItems.indexOfFirst {
                                it.day == backupDataItem.day
                            }

                            if (existingBackupDataItemIndex >= 0) {
                                existingBackupDataItems[existingBackupDataItemIndex] =
                                    backupDataItem
                            } else {
                                existingBackupDataItems.add(backupDataItem)
                            }
                        }
                        DiaryItemStatus.DELETED -> {
                            val matchedIndex =
                                existingBackupDataItems.indexOfFirst { backupDataItem ->
                                    val backupDataItemDate = LocalDate.parse(
                                        backupDataItem.day,
                                        BackupDataDateFormatter
                                    )
                                    backupDataItemDate == diaryItem.date
                                }
                            if (matchedIndex >= 0) {
                                existingBackupDataItems.removeAt(matchedIndex)
                            }
                        }
                    }

                    val currentProgress = progress.increment()
                    send(Result.Loading(currentProgress))
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
    ALREADY_COMPLETED, COMPLETED
}