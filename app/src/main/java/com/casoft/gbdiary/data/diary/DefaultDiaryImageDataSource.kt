package com.casoft.gbdiary.data.diary

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.InputStream

class DefaultDiaryImageDataSource(
    @ApplicationContext private val context: Context,
) : DiaryImageDataSource {

    override fun getImageFile(fileName: String) = File(context.filesDir, fileName)

    override fun copyImageFile(fileName: String, inputStream: InputStream): File {
        return getImageFile(fileName).also {
            inputStream.copyTo(it.outputStream())
        }
    }
}