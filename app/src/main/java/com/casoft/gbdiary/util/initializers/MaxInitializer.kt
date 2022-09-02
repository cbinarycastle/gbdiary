package com.casoft.gbdiary.util.initializers

import android.content.Context
import androidx.startup.Initializer
import com.applovin.sdk.AppLovinSdk

class MaxInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        AppLovinSdk.getInstance(context).apply {
            mediationProvider = "max"
            initializeSdk()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf()
}