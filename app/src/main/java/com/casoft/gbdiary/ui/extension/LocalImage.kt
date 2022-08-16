package com.casoft.gbdiary.ui.extension

import android.content.ContentUris
import android.provider.MediaStore
import com.casoft.gbdiary.model.LocalImage
import java.io.File

fun LocalImage.toContentUri() = ContentUris.withAppendedId(
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    this.id
)

fun LocalImage.toFile() = File(this.path)
