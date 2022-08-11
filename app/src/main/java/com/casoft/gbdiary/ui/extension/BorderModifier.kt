package com.casoft.gbdiary.ui.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.border(
    color: Color,
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
    alpha: Float = 1.0f,
) = drawBehind {
    if (start > 0.dp) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = start.toPx(),
            alpha = alpha
        )
    }
    if (top > 0.dp) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = top.toPx(),
            alpha = alpha
        )
    }
    if (end > 0.dp) {
        drawLine(
            color = color,
            start = Offset(size.width, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = end.toPx(),
            alpha = alpha
        )
    }
    if (bottom > 0.dp) {
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = bottom.toPx(),
            alpha = alpha
        )
    }
}