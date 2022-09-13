package com.casoft.gbdiary.data.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult

enum class BillingState {
    OK,
    BILLING_UNAVAILABLE,
    DEVELOPER_ERROR,
    ERROR,
    FEATURE_NOT_SUPPORTED,
    ITEM_ALREADY_OWNED,
    ITEM_NOT_OWNED,
    ITEM_UNAVAILABLE,
    SERVICE_DISCONNECTED,
    SERVICE_TIMEOUT,
    SERVICE_UNAVAILABLE,
    USER_CANCELED,
}

val BillingResult.billingState: BillingState
    get() = when (responseCode) {
        BillingClient.BillingResponseCode.OK -> BillingState.OK
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> BillingState.BILLING_UNAVAILABLE
        BillingClient.BillingResponseCode.DEVELOPER_ERROR -> BillingState.DEVELOPER_ERROR
        BillingClient.BillingResponseCode.ERROR -> BillingState.ERROR
        BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> BillingState.FEATURE_NOT_SUPPORTED
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> BillingState.ITEM_ALREADY_OWNED
        BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> BillingState.ITEM_NOT_OWNED
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> BillingState.ITEM_UNAVAILABLE
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> BillingState.SERVICE_DISCONNECTED
        BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> BillingState.SERVICE_TIMEOUT
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> BillingState.SERVICE_UNAVAILABLE
        BillingClient.BillingResponseCode.USER_CANCELED -> BillingState.USER_CANCELED
        else -> throw IllegalArgumentException("Unknown code: $this")
    }