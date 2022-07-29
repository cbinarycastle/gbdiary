package com.casoft.gbdiary.data.backup

import android.content.Context
import com.casoft.gbdiary.data.diary.DiaryItemDao
import com.casoft.gbdiary.data.diary.DiaryItemEntity
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
import java.io.ByteArrayOutputStream
import java.util.*

private const val APPLICATION_NAME = "GBDiary"
private const val APP_DATA_FOLDER = "appDataFolder"

private const val MIME_TYPE_JSON = "application/json"
private const val MIME_TYPE_JPEG = "images/jpeg"

private const val JSON_FILE_NAME_PREFIX = "diaryData"
private const val JSON_FILE_NAME_SUFFIX = ".json"
private const val JSON_FILE_NAME = JSON_FILE_NAME_PREFIX + JSON_FILE_NAME_SUFFIX

private const val IMAGE_FILE_EXTENSION = "jpg"

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
                val uploadedImages = uploadImages(
                    drive = drive,
                    date = item.day,
                    images = item.images
                )
                BackupDataItem(
                    day = item.day.format(dateTimeFormatter),
                    contents = item.contents,
                    images = uploadedImages.map { it.id },
                    sticker = item.sticker
                )
            }
        }.awaitAll()

        deleteExistingData(drive = drive)
        uploadData(
            drive = drive,
            backupData = BackupData(backupDataItems)
        )
    }

    private fun uploadImages(drive: Drive, date: LocalDate, images: List<String>): List<File> {
        return images.mapIndexed { index, image ->
            val fileName = "${date}_${index + 1}.$IMAGE_FILE_EXTENSION"
            val filePath = java.io.File(context.filesDir, image)

            deleteExistingImage(drive = drive, fileName = fileName)
            uploadImage(
                drive = drive,
                fileName = fileName,
                filePath = filePath
            )
        }
    }

    private fun deleteExistingImage(drive: Drive, fileName: String) {
        val existingImageFiles = drive.files().list()
            .setQ("name='$fileName'")
            .setSpaces(APP_DATA_FOLDER)
            .execute()

        existingImageFiles.files.forEach {
            drive.files().delete(it.id).execute()
        }
    }

    private fun uploadImage(drive: Drive, fileName: String, filePath: java.io.File): File {
        val metadata = File().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = fileName
        }
        val mediaContent = FileContent(MIME_TYPE_JPEG, filePath)

        return drive.files().create(metadata, mediaContent).execute()
            .also { file -> Timber.d("Uploaded image file ID: ${file.id}") }
    }

    private fun deleteExistingData(drive: Drive) {
        val existingDataFiles = drive.files().list()
            .setQ("name='$JSON_FILE_NAME'")
            .setSpaces(APP_DATA_FOLDER)
            .execute()

        existingDataFiles.files.forEach {
            drive.files().delete(it.id).execute()
        }
    }

    private fun uploadData(drive: Drive, backupData: BackupData) {
        val metadata = File().apply {
            parents = listOf(APP_DATA_FOLDER)
            name = JSON_FILE_NAME
        }
        val mediaContent = FileContent(MIME_TYPE_JSON, createBackupDataFile(backupData))

        val file: File = drive.files().create(metadata, mediaContent).execute()
        Timber.d("Uploaded data file ID: ${file.id}")
    }

    private fun createBackupDataFile(backupData: BackupData): java.io.File {
        val backupDataJson = gson.toJson(backupData)
        return java.io.File.createTempFile(
            JSON_FILE_NAME_PREFIX,
            JSON_FILE_NAME_SUFFIX,
            context.cacheDir
        ).apply { writeText(backupDataJson) }
    }

    override suspend fun sync(credential: GoogleAccountCredential) = withContext(ioDispatcher) {
        val drive = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName(APPLICATION_NAME)
            .build()

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
        val backupData = gson.fromJson(backupDataJson, BackupData::class.java)
        restoreData(drive, backupData)
    }

    private suspend fun restoreData(
        drive: Drive,
        backupData: BackupData,
    ) = withContext(ioDispatcher) {
        val diaryItems = backupData.data.map { item ->
            async {
                val imageFileNames = item.images.map { fileId ->
                    val fileName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.$IMAGE_FILE_EXTENSION"
                    val file = java.io.File(context.filesDir, fileName)
                    ByteArrayOutputStream().let {
                        drive.files().get(fileId).executeMediaAndDownloadTo(it)
                        it.toByteArray().inputStream().copyTo(file.outputStream())
                    }
                    fileName
                }

                DiaryItemEntity(
                    day = LocalDate.parse(item.day, dateTimeFormatter),
                    sticker = item.sticker,
                    contents = item.contents,
                    images = imageFileNames,
                    isSync = true
                )
            }
        }.awaitAll()

        diaryItemDao.deleteAllAndInsertAll(diaryItems)
    }
}