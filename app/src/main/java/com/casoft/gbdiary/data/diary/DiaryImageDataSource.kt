package com.casoft.gbdiary.data.diary

import java.io.File
import java.io.InputStream

interface DiaryImageDataSource {

    fun getImageFile(fileName: String): File

    fun copyImageFile(fileName: String, inputStream: InputStream): File
}