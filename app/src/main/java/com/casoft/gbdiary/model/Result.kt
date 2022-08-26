package com.casoft.gbdiary.model

import androidx.annotation.FloatRange

sealed class Result<out R> {

    data class Success<T>(val data: T) : Result<T>()

    data class Error(val exception: Exception) : Result<Nothing>()

    data class Loading(
        @FloatRange(from = 0.0, to = 1.0) val progress: Float? = null,
    ) : Result<Nothing>() {
        companion object {
            val Start = Loading(0f)
            val End = Loading(1f)
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Loading -> "Loading[progress=$progress]"
        }
    }
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

fun <T> Result<T>.successOr(fallback: T): T {
    return data ?: fallback
}