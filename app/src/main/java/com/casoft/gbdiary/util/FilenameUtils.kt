package com.casoft.gbdiary.util

import java.util.*

fun createUniqueFileName() = "${System.currentTimeMillis()}_${UUID.randomUUID()}"
