package com.casoft.gbdiary.data.diary

import com.casoft.gbdiary.model.LocalImage
import java.io.File
import java.io.InputStream

interface ImageDataSource {

    suspend fun getLocalImages(): List<LocalImage>

    fun getImageFile(fileName: String): File

    fun copyTo(fileName: String, source: InputStream): File
}