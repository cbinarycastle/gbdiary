package com.casoft.gbdiary.data.backup

import android.accounts.Account
import com.google.api.services.drive.model.File
import java.io.InputStream

interface BackupDataSource {

    suspend fun getData(account: Account): BackupData

    suspend fun download(account: Account, fileId: String): InputStream

    suspend fun uploadData(account: Account, backupData: BackupData)

    suspend fun uploadImage(account: Account, fileName: String, filePath: java.io.File): File

    suspend fun deleteData(account: Account)

    suspend fun deleteImage(account: Account, fileName: String)
}