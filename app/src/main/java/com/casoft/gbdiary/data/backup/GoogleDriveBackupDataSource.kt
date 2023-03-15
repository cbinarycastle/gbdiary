package com.casoft.gbdiary.data.backup

import android.accounts.Account
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.casoft.gbdiary.di.PreferencesKeys
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import com.google.api.services.drive.model.File as DriveFile

private const val APPLICATION_NAME = "GBDiary"

private const val APP_DATA_FOLDER = "appDataFolder"

private const val MIME_TYPE_JSON = "application/json"
private const val MIME_TYPE_JPEG = "images/jpeg"

private const val JSON_FILE_NAME = "diaryData"

class GoogleDriveBackupDataSource(
    private val context: Context,
    externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val preferencesDataStore: DataStore<Preferences>,
    private val gson: Gson = Gson(),
) : BackupDataSource {

    override val latestBackupDateTime: Flow<ZonedDateTime?> = preferencesDataStore.data
        .map { prefs ->
            prefs[PreferencesKeys.LATEST_BACKUP_DATE]?.let { epochMilli ->
                Instant.ofEpochMilli(epochMilli).atZone(ZoneOffset.UTC)
            }
        }
        .stateIn(
            scope = externalScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    override suspend fun getAllFiles(account: Account): List<DriveFile> {
        val drive = createDrive(account)
        return drive.files().list()
            .setSpaces(APP_DATA_FOLDER)
            .execute()
            .files
    }

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

    override suspend fun download(
        account: Account,
        fileId: String,
    ): InputStream = withContext(ioDispatcher) {
        val drive = createDrive(account)
        ByteArrayOutputStream().also {
            drive.files().get(fileId).executeMediaAndDownloadTo(it)
        }.toByteArray().inputStream()
    }

    override suspend fun uploadData(
        account: Account,
        backupData: BackupData,
    ) = withContext(ioDispatcher) {
        val drive = createDrive(account)
        val metadata = DriveFile().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = JSON_FILE_NAME
        }
        val mediaContent = FileContent(MIME_TYPE_JSON, createBackupDataFile(backupData))

        val file = drive.files().create(metadata, mediaContent).execute()
        Timber.d("Uploaded data file ID: ${file.id}")
    }

    override suspend fun uploadImage(
        account: Account,
        filename: String,
        file: File,
    ): DriveFile = withContext(ioDispatcher) {
        val drive = createDrive(account)
        val metadata = DriveFile().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = filename
        }
        val mediaContent = FileContent(MIME_TYPE_JPEG, file)

        drive.files().create(metadata, mediaContent).execute()
            .also { file -> Timber.d("Uploaded image file ID: ${file.id}") }
    }

    override suspend fun deleteImage(
        account: Account,
        filename: String,
    ) = withContext(ioDispatcher) {
        val drive = createDrive(account)
        val existingImageFiles = drive.files().list()
            .setQ("name='$filename'")
            .setSpaces(APP_DATA_FOLDER)
            .execute()

        existingImageFiles.files.forEach {
            drive.files().delete(it.id).execute()
        }
    }

    override suspend fun deleteFile(account: Account, fileId: String) {
        val drive = createDrive(account)
        drive.files().delete(fileId)
    }

    override suspend fun setLatestBackupDateTime(dateTime: ZonedDateTime) {
        preferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.LATEST_BACKUP_DATE] = dateTime.toInstant().toEpochMilli()
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

    private fun createBackupDataFile(backupData: BackupData): File {
        val backupDataJson = gson.toJson(backupData)
        return File.createTempFile(
            JSON_FILE_NAME,
            null,
            context.cacheDir
        ).apply { writeText(backupDataJson) }
    }
}