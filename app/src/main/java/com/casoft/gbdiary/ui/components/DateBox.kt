package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.casoft.gbdiary.ui.modifier.alignTopToCenterOfParent
import com.casoft.gbdiary.ui.theme.GBDiaryContentAlpha
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.markerPainter

@Composable
fun DateBox(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isToday: Boolean = false,
) {
    Box(modifier) {
        if (isToday) {
            TodayMarker()
        }
        Text(
            text = text,
            style = GBDiaryTheme.typography.caption,
            modifier = Modifier
                .align(Alignment.Center)
                .then(
                    if (enabled) {
                        Modifier
                    } else {
                        Modifier.alpha(GBDiaryContentAlpha.disabled)
                    }
                )
        )
        if (enabled) {
            ClickableArea(
                onClick = onClick,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TodayMarker(modifier: Modifier = Modifier) {
    Image(
        painter = markerPainter(),
        contentDescription = null,
        modifier = modifier.alignTopToCenterOfParent()
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ClickableArea(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = Color.Transparent,
        content = {}
    )
}