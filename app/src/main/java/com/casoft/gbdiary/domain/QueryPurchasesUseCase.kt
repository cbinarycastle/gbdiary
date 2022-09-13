package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.billing.BillingDataSource
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class QueryPurchasesUseCase @Inject constructor(
    private val billingDataSource: BillingDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Unit, Unit>(ioDispatcher) {

    override suspend fun execute(params: Unit) {
        billingDataSource.queryPurchases()
    }
}