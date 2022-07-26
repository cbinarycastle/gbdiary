package com.casoft.gbdiary.data.backup

import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

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
) : BackupDataSource {

    override suspend fun backup(credential: GoogleAccountCredential) = withContext(ioDispatcher) {
        val drive = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        )
            .setApplicationName(APPLICATION_NAME)
            .build()

        uploadImages(drive)
        uploadJson(drive)
    }

    private fun uploadImages(drive: Drive) {
        val metadata = File().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = "2022-07-28_1.jpg"
        }
        val image = java.io.File(context.filesDir, "sample.png")
        val mediaContent = FileContent(MIME_TYPE_JPEG, image)

        val file: File = drive.files().create(metadata, mediaContent).execute()
            ?: throw IOException("Null result when requesting file creation.")

        Timber.d("Uploaded image file ID: ${file.id}")
    }

    private fun uploadJson(drive: Drive) {
        val metadata = File().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = JSON_FILE_NAME
        }
        val mediaContent = FileContent(MIME_TYPE_JSON, createBackupDataFile())

        val file: File = drive.files().create(metadata, mediaContent).execute()
            ?: throw IOException("Null result when requesting file creation.")

        Timber.d("Uploaded JSON file ID: ${file.id}")
    }

    private fun createBackupDataFile(): java.io.File {
        val backupDataJson = Gson().toJson(createBackupData())

        return java.io.File.createTempFile(
            JSON_FILE_NAME_PREFIX,
            JSON_FILE_NAME_SUFFIX,
            context.cacheDir
        ).apply { writeText(backupDataJson) }
    }

    private fun createBackupData(): BackupData {
        return BackupData(
            data = listOf(
                BackupDataItem(
                    day = "2022-07-28",
                    contents = "오늘은 7월 28일",
                    images = listOf("test.jpg"),
                    sticker = listOf(1, 2)
                )
            )
        )
    }
}