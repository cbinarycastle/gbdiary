package com.casoft.gbdiary.time

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

interface TimeProvider {

    fun now(): Instant

    fun nowLocal(): LocalDateTime

    fun zone(): ZoneId
}

object DefaultTimeProvider : TimeProvider {

    override fun now(): Instant = Instant.now()

    override fun nowLocal(): LocalDateTime = LocalDateTime.now()

    override fun zone(): ZoneId = ZoneId.systemDefault()
}