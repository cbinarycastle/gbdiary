package com.casoft.gbdiary.data.backup

import android.accounts.Account
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import java.io.InputStream

class BackupDataSourceStub : BackupDataSource {

    var backupData: BackupData? = null
        private set
    private val images = mutableMapOf<String, java.io.File>()

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
        filePath: java.io.File,
    ): File {
        images[fileName] = filePath
        return File()
    }

    override suspend fun deleteData(account: Account) {
        backupData = null
    }

    override suspend fun deleteImage(account: Account, fileName: String) {
        images.remove(fileName)
    }

    fun setupTestData() {
        backupData = BackupData(
            data = listOf(
                BackupDataItem(
                    day = "2022-07-31",
                    contents = "22년 7월 31일",
                    images = listOf("abcd.jpg"),
                    sticker = listOf(1, 2, 4)
                ),
                BackupDataItem(
                    day = "2022-08-01",
                    contents = "22년 8월 1일",
                    images = listOf(
                        "efgh.jpg",
                        "ijkl.jpg"
                    ),
                    sticker = listOf(5)
                ),
            )
        )
    }
}