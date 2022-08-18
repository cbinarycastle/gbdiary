package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.LocalImage
import java.io.File
import java.io.InputStream

class ImageDataSourceStub : ImageDataSource {

    private val _files = mutableListOf<File>()
    val files
        get() = _files.toList()

    override suspend fun getLocalImages(): List<LocalImage> = listOf(
        LocalImage(id = 1, path = ""),
        LocalImage(id = 2, path = ""),
        LocalImage(id = 3, path = ""),
    )

    override fun getImageFile(fileName: String): File {
        return File.createTempFile("temp", ".jpg")
    }

    override fun copyTo(fileName: String, source: InputStream): File {
        return getImageFile(fileName).also {
            source.copyTo(it.outputStream())
            _files.add(it)
        }
    }
}