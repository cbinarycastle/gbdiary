package com.casoft.gbdiary.data.billing

import com.android.billingclient.api.Purchase

enum class BillingProductState {
    PENDING, UNPURCHASED, PURCHASED, PURCHASED_AND_ACKNOWLEDGED
}

val Purchase.billingProductState
    get() = when (purchaseState) {
        Purchase.PurchaseState.PURCHASED -> {
            if (isAcknowledged) {
                BillingProductState.PURCHASED_AND_ACKNOWLEDGED
            } else {
                BillingProductState.PURCHASED
            }
        }
        Purchase.PurchaseState.PENDING -> BillingProductState.PENDING
        Purchase.PurchaseState.UNSPECIFIED_STATE -> BillingProductState.UNPURCHASED
        else -> throw IllegalArgumentException("Unknown code: $purchaseState")
    }