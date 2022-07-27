package com.casoft.gbdiary.domain

import android.accounts.Account
import com.casoft.gbdiary.data.auth.AccountDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveAccountUseCase @Inject constructor(
    private val accountDataSource: AccountDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, Account?>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<Account?>> {
        return accountDataSource.account.map { Result.Success(it) }
    }
}