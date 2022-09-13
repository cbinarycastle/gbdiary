package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.billing.BillingDataSource
import com.casoft.gbdiary.data.billing.BillingState
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveBillingStateUseCase @Inject constructor(
    private val billingDataSource: BillingDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, BillingState>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<BillingState>> {
        return billingDataSource.billingState
            .map { Result.Success(it) }
    }
}