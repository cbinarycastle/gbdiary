package com.casoft.gbdiary.data.backup

import android.accounts.Account
import com.google.api.services.drive.model.File
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.time.LocalDate

interface BackupDataSource {

    val latestBackupDate: Flow<LocalDate?>

    suspend fun getAllFiles(account: Account): List<File>

    suspend fun getData(account: Account): BackupData

    suspend fun download(account: Account, fileId: String): InputStream

    suspend fun uploadData(account: Account, backupData: BackupData)

    suspend fun uploadImage(account: Account, fileName: String, filePath: String): File

    suspend fun deleteImage(account: Account, fileName: String)

    suspend fun deleteFile(account: Account, fileId: String)

    suspend fun setLatestBackupDate(date: LocalDate)
}