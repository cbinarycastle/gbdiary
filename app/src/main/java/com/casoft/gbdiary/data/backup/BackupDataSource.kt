package com.casoft.gbdiary.data.backup

import android.accounts.Account
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream
import java.time.ZonedDateTime
import com.google.api.services.drive.model.File as DriveFile

interface BackupDataSource {

    val latestBackupDateTime: Flow<ZonedDateTime?>

    suspend fun getAllFiles(account: Account): List<DriveFile>

    suspend fun getData(account: Account): BackupData

    suspend fun download(account: Account, fileId: String): InputStream

    suspend fun uploadData(account: Account, backupData: BackupData)

    suspend fun uploadImage(account: Account, filename: String, file: File): DriveFile

    suspend fun deleteImage(account: Account, filename: String)

    suspend fun deleteFile(account: Account, fileId: String)

    suspend fun setLatestBackupDateTime(dateTime: ZonedDateTime)
}