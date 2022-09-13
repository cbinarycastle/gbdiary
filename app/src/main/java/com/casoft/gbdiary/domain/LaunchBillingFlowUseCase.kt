package com.casoft.gbdiary.domain

import android.app.Activity
import com.casoft.gbdiary.data.billing.BillingDataSource
import com.casoft.gbdiary.data.billing.BillingProduct
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class LaunchBillingFlowUseCase @Inject constructor(
    private val billingDataSource: BillingDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<LaunchBillingFlowUseCase.Params, Unit>(ioDispatcher) {

    override suspend fun execute(params: Params) {
        billingDataSource.launchBillingFlow(
            product = params.product,
            activity = params.activity
        )
    }

    data class Params(
        val product: BillingProduct,
        val activity: Activity,
    )
}