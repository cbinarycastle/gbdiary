package io.github.cbinarycastle.diary.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun <T> Flow<T>.throttle(delay: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEmissionTime > delay) {
            lastEmissionTime = currentTime
            emit(it)
        }
    }
}


fun <T> Flow<T>.throttle(delay: Duration): Flow<T> = throttle(delay.inWholeMilliseconds)
