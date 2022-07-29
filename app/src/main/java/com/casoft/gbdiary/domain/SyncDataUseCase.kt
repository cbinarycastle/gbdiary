package com.casoft.gbdiary.domain

import android.accounts.Account
import android.content.Context
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.toDiaryItemEntity
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.IMAGE_FILE_EXTENSION
import com.casoft.gbdiary.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.util.*
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    private val diaryDataSource: DiaryDataSource,
    @ApplicationContext private val context: Context,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Account, Unit>(ioDispatcher) {

    override suspend fun execute(params: Account) = coroutineScope {
        val backupData = backupDataSource.getData(account = params)
        val diaryItems = backupData.data
            .map { backupDataItem ->
                async {
                    val imageFileNames = backupDataItem.images.map { fileId ->
                        val fileName =
                            "${System.currentTimeMillis()}_${UUID.randomUUID()}.$IMAGE_FILE_EXTENSION"
                        val file = File(context.filesDir, fileName)
                        backupDataSource.download(account = params, fileId = fileId)
                            .copyTo(file.outputStream())
                        fileName
                    }

                    backupDataItem.toDiaryItemEntity(images = imageFileNames)
                }
            }
            .awaitAll()

        diaryDataSource.deleteAllAndInsertAll(diaryItems)
    }
}