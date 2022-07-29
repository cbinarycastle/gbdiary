package com.casoft.gbdiary.data.backup

import android.content.Context
import com.casoft.gbdiary.data.diary.DiaryItemDao
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
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
    private val diaryItemDao: DiaryItemDao,
    private val gson: Gson = Gson(),
) : BackupDataSource {

    override suspend fun backup(credential: GoogleAccountCredential) = withContext(ioDispatcher) {
        val drive = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName(APPLICATION_NAME)
            .build()

        val diaryItems = diaryItemDao.getNotSyncedDiaryItems()
        val backupDataItems = diaryItems.map { item ->
            async {
                val uploadedImages = uploadImages(drive, item.day, item.images)
                BackupDataItem(
                    day = item.day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    contents = item.contents,
                    images = uploadedImages.map { it.id },
                    sticker = item.sticker
                )
            }
        }.awaitAll()

        uploadJson(
            drive = drive,
            backupData = BackupData(backupDataItems)
        )
    }

    private fun uploadImages(drive: Drive, date: LocalDate, images: List<String>): List<File> {
        return images.mapIndexed { index, image ->
            val metadata = File().apply {
                parents = listOf(APP_DATA_FOLDER)
                name = "${date}_${index + 1}.jpg"
            }
            val filePath = java.io.File(context.filesDir, image)
            val mediaContent = FileContent(MIME_TYPE_JPEG, filePath)

            val file: File = drive.files().create(metadata, mediaContent).execute()
                ?: throw IOException("Null result when requesting file creation.")

            Timber.d("Uploaded image file ID: ${file.id}")

            file
        }
    }

    private fun uploadJson(drive: Drive, backupData: BackupData) {
        val metadata = File().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = JSON_FILE_NAME
        }
        val mediaContent = FileContent(MIME_TYPE_JSON, createBackupDataFile(backupData))

        val file: File = drive.files().create(metadata, mediaContent).execute()
            ?: throw IOException("Null result when requesting file creation.")

        Timber.d("Uploaded JSON file ID: ${file.id}")
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