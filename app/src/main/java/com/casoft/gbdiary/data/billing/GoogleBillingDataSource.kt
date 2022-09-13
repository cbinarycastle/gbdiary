package com.casoft.gbdiary.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.casoft.gbdiary.util.throttle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

private val reconnectionThrottleDelay = 10.seconds

class GoogleBillingDataSource(
    context: Context,
    private val externalScope: CoroutineScope,
) : BillingDataSource, BillingClientStateListener, PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val reconnectSignal = MutableSharedFlow<Unit>()

    private val _billingState = MutableSharedFlow<BillingState>()
    override val billingState = _billingState.asSharedFlow()

    private val productStateMap: MutableMap<String, MutableStateFlow<BillingProductState>> =
        BillingProduct.values()
            .map { it.id }
            .associateWith { MutableStateFlow(BillingProductState.UNPURCHASED) }
            .toMutableMap()

    private val productDetailsMap: MutableMap<String, MutableStateFlow<ProductDetails?>> =
        BillingProduct.values()
            .map { it.id }
            .associateWith { MutableStateFlow<ProductDetails?>(null) }
            .toMutableMap()

    init {
        startConnection()

        externalScope.launch {
            reconnectSignal
                .throttle(reconnectionThrottleDelay)
                .collect { startConnection() }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        externalScope.launch {
            val billingState = billingResult.billingState
            if (billingState == BillingState.OK && purchases != null) {
                processPurchases(purchases)
            }

            _billingState.emit(billingState)
        }
    }

    private suspend fun processPurchases(purchases: List<Purchase>) {
        purchases.forEach { purchase ->
            val productState = purchase.billingProductState
            updateProductStates(purchase, productState)

            if (productState == BillingProductState.PURCHASED) {
                val result = acknowledgePurchase(purchase)
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    updateProductStates(purchase, BillingProductState.PURCHASED_AND_ACKNOWLEDGED)
                }
            }
        }
    }

    private fun updateProductStates(purchase: Purchase, state: BillingProductState) {
        purchase.products.forEach { productId ->
            productStateMap[productId]?.value = state
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase): BillingResult {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        return billingClient.acknowledgePurchase(params)
    }

    private fun startConnection() {
        billingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            externalScope.launch {
                queryProductDetails()
                queryPurchases()
            }
        } else {
            Timber.e("Billing client connection failed. responseCode: ${billingResult.responseCode}")
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.i("Trying to reconnect billing client")
        externalScope.launch {
            reconnectSignal.emit(Unit)
        }
    }

    private suspend fun queryProductDetails() {
        val products = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(BillingProduct.PREMIUM.id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()

        val result = billingClient.queryProductDetails(params)

        onProductDetailsResult(result)
    }

    private fun onProductDetailsResult(result: ProductDetailsResult) {
        result.productDetailsList?.forEach { productDetails ->
            val productDetailsFlow = productDetailsMap[productDetails.productId]
            productDetailsFlow?.value = productDetails
        }
    }

    override suspend fun queryPurchases() {
        if (billingClient.isReady.not()) {
            return
        }

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        val result = billingClient.queryPurchasesAsync(params)

        onPurchasesResult(result)
    }

    private fun onPurchasesResult(result: PurchasesResult) {
        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            externalScope.launch {
                processPurchases(result.purchasesList)
            }
        } else {
            Timber.e("Querying purchases is failed")
        }
    }

    override fun isPurchased(product: BillingProduct): Flow<Boolean> {
        val productStateFlow = productStateMap[product.id]
            ?: return flowOf(false)

        return productStateFlow.map { it == BillingProductState.PURCHASED_AND_ACKNOWLEDGED }
    }

    override fun canPurchase(product: BillingProduct): Flow<Boolean> {
        val productStateFlow = productStateMap[product.id]
        val productDetailsFlow = productDetailsMap[product.id]

        if (productStateFlow == null || productDetailsFlow == null) {
            return flowOf(false)
        }

        return combine(productStateFlow, productDetailsFlow) { productState, productDetails ->
            productState == BillingProductState.UNPURCHASED && productDetails != null
        }
    }

    override fun launchBillingFlow(product: BillingProduct, activity: Activity) {
        val productDetails = productDetailsMap[product.id]?.value
        if (productDetails == null) {
            /* TODO */
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val result = billingClient.launchBillingFlow(activity, params)

        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            externalScope.launch {
                _billingState.emit(result.billingState)
            }
        }
    }
}