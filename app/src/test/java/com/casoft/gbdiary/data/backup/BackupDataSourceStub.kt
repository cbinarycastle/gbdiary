package com.casoft.gbdiary.data.backup

import android.accounts.Account
import com.casoft.gbdiary.model.Sticker
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.InputStream
import java.time.ZonedDateTime
import com.google.api.services.drive.model.File as DriveFile

class BackupDataSourceStub : BackupDataSource {

    var backupData: BackupData? = null
        private set

    private val images = mutableMapOf<String, File>()

    private val _latestBackupDate = MutableStateFlow<ZonedDateTime?>(null)
    override val latestBackupDateTime: Flow<ZonedDateTime?> = _latestBackupDate.asStateFlow()

    override suspend fun getAllFiles(account: Account): List<DriveFile> = listOf()

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
        filename: String,
        file: File,
    ): DriveFile {
        images[filename] = file
        return DriveFile()
    }

    override suspend fun deleteImage(account: Account, filename: String) {
        images.remove(filename)
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