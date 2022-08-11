package com.casoft.gbdiary.data.diary

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.casoft.gbdiary.model.LocalImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class DefaultImageDataSource(
    @ApplicationContext private val context: Context,
) : ImageDataSource {

    override suspend fun getLocalImages(): List<LocalImage> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(MediaStore.Images.Media._ID)

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val images = mutableListOf<LocalImage>()
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                images += LocalImage(id)
            }
            images
        } ?: listOf()
    }

    override fun getImageFile(fileName: String) = File(context.filesDir, fileName)

    override fun copyImageFile(fileName: String, inputStream: InputStream): File {
        return getImageFile(fileName).also {
            inputStream.copyTo(it.outputStream())
        }
    }
}