package com.casoft.gbdiary.data.diary

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.casoft.gbdiary.model.LocalImage
import com.casoft.gbdiary.util.createUniqueFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class LocalImageDataSource(
    @ApplicationContext private val context: Context,
) : ImageDataSource {

    override suspend fun getLocalImages(): List<LocalImage> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
        val sortOrder = "${MediaStore.Images.ImageColumns.DATE_TAKEN} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val images = mutableListOf<LocalImage>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                images += LocalImage(id, path)
            }
            images
        } ?: listOf()
    }

    override fun saveImage(source: InputStream): File {
        return File(context.filesDir, createUniqueFileName()).also {
            source.copyTo(it.outputStream())
        }
    }
}