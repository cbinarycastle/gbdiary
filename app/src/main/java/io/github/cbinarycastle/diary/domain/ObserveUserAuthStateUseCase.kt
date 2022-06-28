package io.github.cbinarycastle.diary.domain

import io.github.cbinarycastle.diary.data.UserDataSource
import io.github.cbinarycastle.diary.di.IoDispatcher
import io.github.cbinarycastle.diary.model.Result
import io.github.cbinarycastle.diary.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveUserAuthStateUseCase @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, User?>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<User?>> {
        return userDataSource.user.map { Result.Success(it) }
    }
}