package com.casoft.gbdiary.data.backup

import android.accounts.Account
import com.casoft.gbdiary.model.Sticker
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import java.time.ZonedDateTime

class BackupDataSourceStub : BackupDataSource {

    var backupData: BackupData? = null
        private set

    private val images = mutableMapOf<String, String>()

    private val _latestBackupDate = MutableStateFlow<ZonedDateTime?>(null)
    override val latestBackupDateTime: Flow<ZonedDateTime?> = _latestBackupDate.asStateFlow()

    override suspend fun getAllFiles(account: Account): List<File> = listOf()

    override suspend fun getData(account: Account): BackupData {
        return backupData ?: throw BackupDataNotFoundException()
    }

    override suspend fun download(account: Account, fileId: String): InputStream =
        Gson().toJson(backupData).byteInputStream()

    override suspend fun uploadData(account: Account, backupData: BackupData) {
        this.backupData = backupData.copy()
    }

    override suspend fun uploadImage(
        account: Account,
        fileName: String,
        filePath: String,
    ): File {
        images[fileName] = filePath
        return File()
    }

    override suspend fun deleteImage(account: Account, fileName: String) {
        images.remove(fileName)
    }

    override suspend fun deleteFile(account: Account, fileId: String) {
    }

    override suspend fun setLatestBackupDateTime(dateTime: ZonedDateTime) {
        _latestBackupDate.value = dateTime
    }

    fun setupTestData() {
        backupData = BackupData(
            data = listOf(
                BackupDataItem(
                    day = "2022-07-31",
                    contents = "22년 7월 31일",
                    images = listOf("abcd.jpg"),
                    sticker = listOf(
                        Sticker.ANGER.name,
                        Sticker.CONFUSION.name,
                        Sticker.DEPRESSION.name
                    )
                ),
                BackupDataItem(
                    day = "2022-08-01",
                    contents = "22년 8월 1일",
                    images = listOf(
                        "efgh.jpg",
                        "ijkl.jpg"
                    ),
                    sticker = listOf(Sticker.HOPEFUL.name)
                ),
            )
        )
    }
}