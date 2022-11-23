package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun TickSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    steps: Int,
) {
    val tickFractions = remember(steps) { stepsToTickFractions(steps) }

    BoxWithConstraints {
        val thumbRadiusPx: Float
        val maxPx: Float
        val minPx: Float

        with(LocalDensity.current) {
            thumbRadiusPx = ThumbRadius.toPx()
            maxPx = max(constraints.maxWidth - thumbRadiusPx, 0f)
            minPx = min(thumbRadiusPx, maxPx)
        }
        val width = maxPx - minPx

        var pressOffset by remember { mutableStateOf(0f) }
        var rawOffset by remember { mutableStateOf(valueToThumbOffset(value, width, steps)) }

        val draggableState = rememberDraggableState {
            rawOffset += it + pressOffset
            pressOffset = 0f

            val targetValue = snapOffsetToTick(
                minPx = minPx,
                maxPx = maxPx,
                rawOffset = rawOffset + thumbRadiusPx,
                tickFractions = tickFractions
            )
            if (value != targetValue) {
                onValueChange(targetValue)
            }
        }

        val thumbOffset = with(LocalDensity.current) {
            valueToThumbOffset(value, width, steps).toDp()
        }

        GBDiarySliderImpl(
            tickFractions = tickFractions,
            thumbModifier = Modifier
                .offset(x = thumbOffset)
                .pointerInput(true) {
                    detectTapGestures(onPress = { offset -> pressOffset = offset.x })
                }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = { rawOffset = valueToThumbOffset(value, width, steps) }
                )
        )
    }
}

private fun stepsToTickFractions(steps: Int): List<Float> =
    (0 until steps).map { it.toFloat() / (steps - 1) }

@Composable
private fun GBDiarySliderImpl(
    tickFractions: List<Float>,
    modifier: Modifier = Modifier,
    thumbModifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
    ) {
        Track(tickFractions)
        Thumb(thumbModifier)
    }
}

private fun snapOffsetToTick(
    minPx: Float,
    maxPx: Float,
    rawOffset: Float,
    tickFractions: List<Float>,
): Int {
    var minValue = Float.MAX_VALUE
    var minIndex = 0

    tickFractions
        .map { abs(lerp(minPx, maxPx, it) - rawOffset) }
        .forEachIndexed { index, value ->
            if (value < minValue) {
                minValue = value
                minIndex = index
            }
        }

    return minIndex + 1
}

private fun valueToThumbOffset(value: Int, width: Float, steps: Int): Float {
    return width / (steps - 1) * (value - 1)
}

@Composable
private fun Track(
    tickFractions: List<Float>,
    modifier: Modifier = Modifier,
) {
    val color = GBDiaryTheme.gbDiaryColors.border

    Canvas(modifier.fillMaxWidth()) {
        val sliderStart = ThumbRadius.toPx()
        val sliderEnd = size.width - sliderStart

        drawLine(
            color = color,
            start = Offset(x = sliderStart, y = center.y),
            end = Offset(x = sliderEnd, y = center.y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        tickFractions
            .map {
                Offset(
                    x = lerp(start = sliderStart, stop = sliderEnd, fraction = it),
                    y = center.y
                )
            }
            .let {
                drawPoints(
                    points = it,
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
    }
}

@Composable
private fun Thumb(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .size(ThumbSize - ThumbBorderWidth)
            .clip(CircleShape)
            .background(color = GBDiaryTheme.colors.primary)
            .border(
                width = ThumbBorderWidth,
                color = GBDiaryTheme.gbDiaryColors.enabled,
                shape = CircleShape
            )
    )
}

private val ThumbRadius = 10.dp
private val ThumbSize = ThumbRadius * 2
private val ThumbBorderWidth = 1.8.dp