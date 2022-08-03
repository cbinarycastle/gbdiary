package com.casoft.gbdiary.data.diary

import java.io.File
import java.io.InputStream

class DiaryImageDataSourceStub : DiaryImageDataSource {

    private val _files = mutableListOf<File>()
    val files
        get() = _files.toList()

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