package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.toDiaryItemEntity
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.IMAGE_FILE_EXTENSION
import com.casoft.gbdiary.data.diary.ImageDataSource
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    private val imageDataSource: ImageDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Account, Unit>(ioDispatcher) {

    private val uniqueFileName
        get() = "${System.currentTimeMillis()}_${UUID.randomUUID()}.$IMAGE_FILE_EXTENSION"

    override suspend fun execute(params: Account) = coroutineScope {
        val backupData = backupDataSource.getData(account = params)
        val diaryItems = backupData.data
            .map { backupDataItem ->
                async {
                    val imageFileNames = backupDataItem.images.map { fileId ->
                        backupDataSource.download(account = params, fileId = fileId)
                            .let { imageDataSource.copyImageFile(uniqueFileName, it) }
                            .name
                    }

                    backupDataItem.toDiaryItemEntity(images = imageFileNames)
                }
            }
            .awaitAll()

        diaryDataSource.deleteAllAndInsertAll(diaryItems)
    }
}