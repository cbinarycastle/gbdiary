package com.casoft.gbdiary.ui.components

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.applovin.mediation.ads.MaxAdView
import com.casoft.gbdiary.util.dpToPx

const val AD_HEIGHT = 50

@Composable
fun AdBanner(
    adUnitId: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            MaxAdView(adUnitId, context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, AD_HEIGHT.dpToPx)
                loadAd()
            }
        },
        modifier = modifier
    )
}