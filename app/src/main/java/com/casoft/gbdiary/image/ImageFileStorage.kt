package com.casoft.gbdiary.image

import android.content.ContentResolver
import com.casoft.gbdiary.di.ImageFilesDir
import com.casoft.gbdiary.model.LocalImage
import com.casoft.gbdiary.util.generateUniqueFileName
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class ImageFileStorage @Inject constructor(
    private val contentResolver: ContentResolver,
    @ImageFilesDir private val imageFilesDir: File,
) {
    fun save(images: List<LocalImage>): List<File> {
        if (!imageFilesDir.exists()) {
            imageFilesDir.mkdirs()
        }

        return images.mapNotNull { image ->
            contentResolver.openInputStream(image.contentUri)?.use { inputStream ->
                save(inputStream)
            }
        }
    }

    fun save(source: InputStream): File {
        return File(imageFilesDir, generateUniqueFileName()).also { destination ->
            source.copyTo(destination.outputStream())
        }
    }
}