package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.auth.AccountDataSource
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CheckExistingSignedInUserUseCase @Inject constructor(
    private val accountDataSource: AccountDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : UseCase<Unit, Unit>(ioDispatcher) {

    override suspend fun execute(params: Unit) {
        accountDataSource.checkExistingSignedInUser()
    }
}