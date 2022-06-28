package io.github.cbinarycastle.diary.domain

import io.github.cbinarycastle.diary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<P, R>(private val dispatcher: CoroutineDispatcher) {

    operator fun invoke(params: P): Flow<Result<R>> {
        return execute(params)
            .catch { emit(Result.Error(Exception(it))) }
            .flowOn(dispatcher)
    }

    abstract fun execute(params: P): Flow<Result<R>>
}