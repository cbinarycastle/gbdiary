package com.casoft.gbdiary.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.ui.modifier.noRippleClickable
import com.casoft.gbdiary.ui.theme.DarkTextIcon
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import kotlin.math.roundToInt

@Composable
fun Progress(progress: Float) {
    val color = DarkTextIcon
    val indicatorBackgroundColor = color.copy(alpha = 0.3f)

    BackHandler { }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(GBDiaryTheme.gbDiaryColors.dimmingOverlay)
            .noRippleClickable { }
    ) {
        Canvas(Modifier.size(72.dp)) {
            drawCircle(
                color = indicatorBackgroundColor,
                style = Stroke(4.dp.toPx())
            )
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = progress * 360,
                useCenter = false,
                style = Stroke(4.dp.toPx())
            )
        }
        Text(
            text = "${(progress * 100).roundToInt()}%",
            color = color,
            style = GBDiaryTheme.typography.subtitle1,
        )
    }
}