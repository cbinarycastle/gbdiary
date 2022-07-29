package com.casoft.gbdiary.data.backup

import android.accounts.Account
import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.InputStream

private const val APPLICATION_NAME = "GBDiary"

private const val APP_DATA_FOLDER = "appDataFolder"

private const val MIME_TYPE_JSON = "application/json"
private const val MIME_TYPE_JPEG = "images/jpeg"

private const val JSON_FILE_NAME_PREFIX = "diaryData"
private const val JSON_FILE_NAME_SUFFIX = ".json"
private const val JSON_FILE_NAME = JSON_FILE_NAME_PREFIX + JSON_FILE_NAME_SUFFIX

class GoogleDriveBackupDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher,
    private val gson: Gson = Gson(),
) : BackupDataSource {

    override suspend fun getData(account: Account): BackupData = withContext(ioDispatcher) {
        val drive = createDrive(account)

        val backupDataFile = drive.files().list()
            .setQ("name='$JSON_FILE_NAME'")
            .setSpaces(APP_DATA_FOLDER)
            .execute()
            .files
            .firstOrNull() ?: throw BackupDataNotFoundException()

        val backupDataJson = ByteArrayOutputStream().let {
            drive.files().get(backupDataFile.id).executeMediaAndDownloadTo(it)
            it.toByteArray().decodeToString()
        }

        gson.fromJson(backupDataJson, BackupData::class.java)
    }

    override suspend fun download(account: Account, fileId: String): InputStream {
        val drive = createDrive(account)
        return ByteArrayOutputStream().also {
            drive.files().get(fileId).executeMediaAndDownloadTo(it)
        }.toByteArray().inputStream()
    }

    override suspend fun uploadData(
        account: Account,
        backupData: BackupData,
    ) = withContext(ioDispatcher) {
        val drive = createDrive(account)
        val metadata = File().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = JSON_FILE_NAME
        }
        val mediaContent = FileContent(MIME_TYPE_JSON, createBackupDataFile(backupData))

        val file: File = drive.files().create(metadata, mediaContent).execute()
        Timber.d("Uploaded data file ID: ${file.id}")
    }

    override suspend fun uploadImage(
        account: Account,
        fileName: String,
        filePath: java.io.File,
    ): File = withContext(ioDispatcher) {
        val drive = createDrive(account)
        val metadata = File().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = fileName
        }
        val mediaContent = FileContent(MIME_TYPE_JPEG, filePath)

        drive.files().create(metadata, mediaContent).execute()
            .also { file -> Timber.d("Uploaded image file ID: ${file.id}") }
    }

    override suspend fun deleteData(account: Account) = withContext(ioDispatcher) {
        val drive = createDrive(account)
        val existingDataFiles = drive.files().list()
            .setQ("name='$JSON_FILE_NAME'")
            .setSpaces(APP_DATA_FOLDER)
            .execute()

        existingDataFiles.files.forEach {
            drive.files().delete(it.id).execute()
        }
    }

    override suspend fun deleteImage(
        account: Account,
        fileName: String,
    ) = withContext(ioDispatcher) {
        val drive = createDrive(account)
        val existingImageFiles = drive.files().list()
            .setQ("name='$fileName'")
            .setSpaces(APP_DATA_FOLDER)
            .execute()

        existingImageFiles.files.forEach {
            drive.files().delete(it.id).execute()
        }
    }

    private fun createDrive(account: Account): Drive {
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            GoogleAccountCredential.usingOAuth2(
                context,
                listOf(DriveScopes.DRIVE_APPDATA)
            ).apply { selectedAccount = account }
        )
            .setApplicationName(APPLICATION_NAME)
            .build()
    }

    private fun createBackupDataFile(backupData: BackupData): java.io.File {
        val backupDataJson = gson.toJson(backupData)
        return java.io.File.createTempFile(
            JSON_FILE_NAME_PREFIX,
            JSON_FILE_NAME_SUFFIX,
            context.cacheDir
        ).apply { writeText(backupDataJson) }
    }
}