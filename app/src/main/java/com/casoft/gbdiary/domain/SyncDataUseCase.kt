package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.toDiaryItemEntity
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.ImageDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.util.uniqueFileName
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    private val imageDataSource: ImageDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Account, Unit>(ioDispatcher) {

    override suspend fun execute(params: Account) = coroutineScope {
        val backupData = backupDataSource.getData(account = params)
        val diaryItems = backupData.data
            .map { backupDataItem ->
                async {
                    val imageFileNames = backupDataItem.images.map { fileId ->
                        backupDataSource.download(account = params, fileId = fileId)
                            .let { inputStream ->
                                imageDataSource.copyTo(
                                    fileName = uniqueFileName,
                                    source = inputStream
                                )
                            }
                            .name
                    }

                    backupDataItem.toDiaryItemEntity(images = imageFileNames)
                }
            }
            .awaitAll()

        diaryDataSource.deleteAllAndInsertAll(diaryItems)
    }
}