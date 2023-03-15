package com.casoft.gbdiary.data.diary

import android.net.Uri
import com.casoft.gbdiary.model.LocalImage
import java.io.File
import java.io.InputStream

class ImageDataSourceStub : ImageDataSource {

    private val _files = mutableListOf<File>()
    val files
        get() = _files.toList()

    override suspend fun getLocalImages(): List<LocalImage> = listOf(
        LocalImage(id = 1, contentUri = Uri.EMPTY),
        LocalImage(id = 2, contentUri = Uri.EMPTY),
        LocalImage(id = 3, contentUri = Uri.EMPTY),
    )

    override fun saveImage(source: InputStream): File {
        return File.createTempFile("temp", ".jpg").also {
            source.copyTo(it.outputStream())
            _files.add(it)
        }
    }
}