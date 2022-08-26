package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import kotlin.math.roundToInt

@Composable
fun ProgressDialog(progress: Float) {
    val progressColor = GBDiaryTheme.colors.onBackground
    val backgroundColor = progressColor.copy(alpha = 0.3f)

    Dialog(onDismissRequest = {}) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(72.dp)) {
                drawCircle(
                    color = backgroundColor,
                    style = Stroke(4.dp.toPx())
                )
                drawArc(
                    color = progressColor,
                    startAngle = 270f,
                    sweepAngle = progress * 360,
                    useCenter = false,
                    style = Stroke(4.dp.toPx())
                )
            }
            Text(
                text = "${(progress * 100).roundToInt()}%",
                style = GBDiaryTheme.typography.subtitle1
            )
        }
    }
}