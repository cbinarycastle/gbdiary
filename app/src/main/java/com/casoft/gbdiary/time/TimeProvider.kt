package com.casoft.gbdiary.time

import java.time.LocalDateTime
import java.time.ZoneId

interface TimeProvider {

    fun now(): LocalDateTime

    fun zone(): ZoneId
}

object DefaultTimeProvider : TimeProvider {

    override fun now(): LocalDateTime = LocalDateTime.now()

    override fun zone(): ZoneId = ZoneId.systemDefault()
}