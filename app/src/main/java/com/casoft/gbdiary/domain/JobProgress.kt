package com.casoft.gbdiary.domain

import androidx.annotation.FloatRange
import java.util.concurrent.atomic.AtomicInteger

class JobProgress(
    numberOfJobs: Int,
    @FloatRange(from = 0.0, to = 1.0) maxProgress: Float = 1f,
) {
    private val finishedJobs = AtomicInteger()
    private val progressUnit = maxProgress / numberOfJobs

    fun increment(): Float = progressUnit * finishedJobs.incrementAndGet()
}