package com.casoft.gbdiary.data.billing

import android.content.Context
import com.android.billingclient.api.*
import com.casoft.gbdiary.extensions.throttle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class GoogleBillingDataSource(
    context: Context,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
) : BillingDataSource, BillingClientStateListener, PurchasesUpdatedListener {

    private val reconnectionThrottleDelay = 10.seconds

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val productDetailsList = mutableListOf<ProductDetails>()

    private val purchases = mutableListOf<Purchase>()

    private val _billingStateCode = MutableSharedFlow<Int>()
    val billingStateCode = _billingStateCode.asSharedFlow()

    private val reconnectSignal = MutableSharedFlow<Unit>()

    init {
        startConnection()

        externalScope.launch {
            reconnectSignal
                .throttle(reconnectionThrottleDelay)
                .collect { startConnection() }
        }
    }

    private fun startConnection() {
        billingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            externalScope.launch {
                queryProductDetails()
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        externalScope.launch {
            reconnectSignal.emit(Unit)
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        externalScope.launch {
            _billingStateCode.emit(billingResult.responseCode)
        }
    }

    private suspend fun queryProductDetails() {
        val params = createQueryProductDetailsParams()
        withContext(ioDispatcher) {
            billingClient.queryProductDetails(params)
        }.let { onProductDetailsResult(it) }
    }

    private fun onProductDetailsResult(result: ProductDetailsResult) {
        result.productDetailsList?.let {
            productDetailsList.run {
                clear()
                addAll(it)
            }
        }
    }

    private suspend fun queryPurchases() {
        val params = createQueryPurchasesParams()
        withContext(ioDispatcher) {
            billingClient.queryPurchasesAsync(params)
        }.let { onPurchasesResult(it) }
    }

    private fun createQueryPurchasesParams() = QueryPurchasesParams.newBuilder()
        .setProductType(BillingClient.ProductType.INAPP)
        .build()

    private fun onPurchasesResult(result: PurchasesResult) {
        purchases.run {
            clear()
            addAll(result.purchasesList)
        }
    }

    private fun createQueryProductDetailsParams(): QueryProductDetailsParams {
        val products = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(InAppItem.PREMIUM.productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        return QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()
    }
}