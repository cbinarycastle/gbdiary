package com.casoft.gbdiary.domain

import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class UseCase<P, R>(private val dispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(params: P): Result<R> {
        return try {
            withContext(dispatcher) {
                execute(params).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    abstract suspend fun execute(params: P): R
}