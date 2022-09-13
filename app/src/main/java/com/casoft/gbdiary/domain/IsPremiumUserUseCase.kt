package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.billing.BillingDataSource
import com.casoft.gbdiary.data.billing.BillingProduct
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsPremiumUserUseCase @Inject constructor(
    private val billingDataSource: BillingDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, Boolean>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<Boolean>> {
        return billingDataSource.isPurchased(BillingProduct.PREMIUM)
            .map { Result.Success(it) }
    }
}