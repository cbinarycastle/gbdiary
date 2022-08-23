package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupData
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.IMAGE_FILE_EXTENSION
import com.casoft.gbdiary.data.diary.ImageDataSource
import com.casoft.gbdiary.data.diary.toDiaryItem
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.toBackupData
import com.google.api.services.drive.model.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import javax.inject.Inject

class BackupDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    private val imageDataSource: ImageDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Account, Unit>(ioDispatcher) {

    override suspend fun execute(params: Account) = coroutineScope {
        val backupDataItems = diaryDataSource.getNotSyncedDiaryItems()
            .map { diaryItemEntity ->
                async {
                    val diaryItem = diaryItemEntity.toDiaryItem()
                    val uploadedImages = uploadImages(
                        account = params,
                        date = diaryItem.date,
                        images = diaryItem.images
                    ).map { it.id }

                    diaryItem.toBackupData(images = uploadedImages)
                }
            }
            .awaitAll()

        backupDataSource.deleteData(account = params)
        backupDataSource.uploadData(
            account = params,
            backupData = BackupData(backupDataItems)
        )
    }

    private suspend fun uploadImages(
        account: Account,
        date: LocalDate,
        images: List<String>,
    ): List<File> {
        return images.mapIndexed { index, fileName ->
            val backupFileName = "${date}_${index + 1}.$IMAGE_FILE_EXTENSION"
            backupDataSource.deleteImage(account = account, fileName = backupFileName)

            val filePath = imageDataSource.getImageFile(fileName)
            backupDataSource.uploadImage(
                account = account,
                fileName = backupFileName,
                filePath = filePath
            )
        }
    }
}