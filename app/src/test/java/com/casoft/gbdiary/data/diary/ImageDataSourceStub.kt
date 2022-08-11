package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.LocalImage
import java.io.File
import java.io.InputStream

class ImageDataSourceStub : ImageDataSource {

    private val _files = mutableListOf<File>()
    val files
        get() = _files.toList()

    override suspend fun getLocalImages(): List<LocalImage> = listOf(
        LocalImage(1),
        LocalImage(2),
        LocalImage(3),
    )

    override fun getImageFile(fileName: String): File {
        return File.createTempFile("temp", ".jpg")
    }

    override fun copyImageFile(fileName: String, inputStream: InputStream): File {
        return getImageFile(fileName).also {
            inputStream.copyTo(it.outputStream())
            _files.add(it)
        }
    }
}