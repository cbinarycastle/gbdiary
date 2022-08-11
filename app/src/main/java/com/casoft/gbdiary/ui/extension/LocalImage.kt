package com.casoft.gbdiary.ui.extension

import android.content.ContentUris
import android.provider.MediaStore
import com.casoft.gbdiary.model.LocalImage

fun LocalImage.contentUri() = ContentUris.withAppendedId(
    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
    this.id
)
