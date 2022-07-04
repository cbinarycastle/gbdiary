package io.github.cbinarycastle.diary.data.billing

import android.content.Context
import com.android.billingclient.api.*
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.cbinarycastle.diary.di.ApplicationScope
import io.github.cbinarycastle.diary.di.IoDispatcher
import io.github.cbinarycastle.diary.extensions.throttle
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.seconds

private const val RECONNECTION_THROTTLE_DELAY = 10

class GoogleBillingDataSource(
    @ApplicationContext applicationContext: Context,
    @ApplicationScope private val externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : BillingDataSource, BillingClientStateListener, PurchasesUpdatedListener {

    private val billingClient = BillingClient.newBuilder(applicationContext)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val productDetailsList = mutableListOf<ProductDetails>()

    private val _billingStateCode = MutableSharedFlow<Int>()
    val billingStateCode = _billingStateCode.asSharedFlow()

    private val reconnectSignal = MutableSharedFlow<Unit>()

    init {
        startConnection()

        externalScope.launch {
            reconnectSignal
                .throttle(RECONNECTION_THROTTLE_DELAY.seconds)
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

    private suspend fun queryPurchases() {
        val params = createQueryPurchasesParams()
        billingClient.queryPurchasesAsync()
    }

    private fun createQueryPurchasesParams() = QueryPurchasesParams.newBuilder()
        .setProductType(BillingClient.ProductType.INAPP)
        .build()

    private fun onProductDetailsResult(result: ProductDetailsResult) {
        result.productDetailsList?.let {
            productDetailsList.run {
                clear()
                addAll(it)
            }
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