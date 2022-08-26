package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.auth.UserDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveUserUseCase @Inject constructor(
    private val userDataSource: UserDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, User?>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<User?>> {
        return userDataSource.user.map { Result.Success(it) }
    }
}