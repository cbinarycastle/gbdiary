package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupData
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.toBackupData
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.DiaryImageDataSource
import com.casoft.gbdiary.data.diary.IMAGE_FILE_EXTENSION
import com.casoft.gbdiary.di.IoDispatcher
import com.google.api.services.drive.model.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.LocalDate
import javax.inject.Inject

class BackupDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    private val diaryImageDataSource: DiaryImageDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Account, Unit>(ioDispatcher) {

    override suspend fun execute(params: Account) = coroutineScope {
        val backupDataItems = diaryDataSource.getNotSyncedDiaryItems()
            .map { diaryItem ->
                async {
                    val uploadedImages = uploadImages(
                        account = params,
                        date = diaryItem.day,
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

            val filePath = diaryImageDataSource.getImageFile(fileName)
            backupDataSource.uploadImage(
                account = account,
                fileName = backupFileName,
                filePath = filePath
            )
        }
    }
}