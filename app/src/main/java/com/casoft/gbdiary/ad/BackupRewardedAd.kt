package com.casoft.gbdiary.ad

import android.app.Activity
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow
import kotlin.time.Duration.Companion.seconds

class BackupRewardedAd @Inject constructor(
    activity: Activity,
    private val coroutineScope: CoroutineScope,
) {
    private val listener = object : MaxRewardedAdListener {

        override fun onAdLoaded(maxAd: MaxAd) {
            Timber.d("binarycastle onAdLoaded")
            retryAttempt = 0
        }

        override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
            Timber.d("binarycastle onAdLoadFailed")
            retryAttempt++

            val delay = retryAttempt.toFloat().coerceAtMost(6f).pow(2).toLong()
            coroutineScope.launch(Dispatchers.Main) {
                delay(delay.seconds)
                maxRewardedAd.loadAd()
            }
        }

        override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
            Timber.d("binarycastle onAdDisplayFailed")
            maxRewardedAd.loadAd()
        }

        override fun onAdDisplayed(maxAd: MaxAd) {}

        override fun onAdClicked(maxAd: MaxAd) {}

        override fun onAdHidden(maxAd: MaxAd) {
            Timber.d("binarycastle onAdHidden")
            maxRewardedAd.loadAd()
        }

        override fun onRewardedVideoStarted(maxAd: MaxAd) {}

        override fun onRewardedVideoCompleted(maxAd: MaxAd) {}

        override fun onUserRewarded(maxAd: MaxAd, maxReward: MaxReward) {
            Timber.d("binarycastle onUserRewarded")
            onUserRewarded?.invoke()
        }
    }

    private val maxRewardedAd: MaxRewardedAd = MaxRewardedAd.getInstance(
        BACKUP_REWARDED_AD_UNIT_ID,
        activity
    ).apply {
        setListener(listener)
        loadAd()
    }

    private var onUserRewarded: (() -> Unit)? = null

    private var retryAttempt = 0

    fun showAd(onUserRewarded: () -> Unit) {
        if (maxRewardedAd.isReady) {
            this.onUserRewarded = onUserRewarded
            maxRewardedAd.showAd()
        }
    }
}