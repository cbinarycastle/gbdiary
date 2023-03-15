package com.casoft.gbdiary.util

import java.util.*

fun generateUniqueFileName() = "${System.currentTimeMillis()}_${UUID.randomUUID()}"
