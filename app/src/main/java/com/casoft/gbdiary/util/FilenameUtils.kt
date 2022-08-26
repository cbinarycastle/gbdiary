package com.casoft.gbdiary.util

import com.casoft.gbdiary.data.diary.IMAGE_FILE_EXTENSION
import java.util.*

fun createUniqueFileName() =
    "${System.currentTimeMillis()}_${UUID.randomUUID()}.$IMAGE_FILE_EXTENSION"
