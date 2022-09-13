package com.casoft.gbdiary.data.billing

import android.app.Activity
import kotlinx.coroutines.flow.Flow

interface BillingDataSource {

    val billingState: Flow<BillingState>

    fun isPurchased(product: BillingProduct): Flow<Boolean>

    fun canPurchase(product: BillingProduct): Flow<Boolean>

    suspend fun queryPurchases()

    fun launchBillingFlow(product: BillingProduct, activity: Activity)
}