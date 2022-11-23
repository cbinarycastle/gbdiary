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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun <T> TickSlider(
    value: T,
    onValueChange: (T) -> Unit,
    steps: List<T>,
    modifier: Modifier = Modifier,
) {
    val tickFractions = remember(steps) { stepsToTickFractions(steps.size) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val hapticFeedback = LocalHapticFeedback.current
        val coroutineScope = rememberCoroutineScope()

        val thumbRadiusPx: Float
        val maxPx: Float
        val minPx: Float
        with(density) {
            thumbRadiusPx = ThumbRadius.toPx()
            maxPx = max(constraints.maxWidth - thumbRadiusPx, 0f)
            minPx = min(thumbRadiusPx, maxPx)
        }
        val width = maxPx - minPx

        var isDragging by remember { mutableStateOf(false) }
        var pressOffset by remember { mutableStateOf(0f) }
        var rawOffset by remember { mutableStateOf(valueToThumbOffset(value, width, steps)) }

        val draggableState = rememberDraggableState {
            rawOffset += it + pressOffset
            pressOffset = 0f

            val targetValue = snapOffsetToTick(
                minPx = minPx,
                maxPx = maxPx,
                rawOffset = rawOffset + minPx,
                steps = steps,
                tickFractions = tickFractions
            )

            if (value != targetValue) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onValueChange(targetValue)
            }
        }

        val thumbOffset = with(density) {
            valueToThumbOffset(value, width, steps).toDp()
        }

        LaunchedEffect(value) {
            if (!isDragging) {
                rawOffset = valueToThumbOffset(value, width, steps)
            }
        }

        GBDiarySliderImpl(
            tickFractions = tickFractions,
            modifier = Modifier.pointerInput(true) {
                detectTapGestures(
                    onTap = { offset ->
                        coroutineScope.launch {
                            draggableState.drag {
                                dragBy((offset.x - minPx) - rawOffset)
                            }
                        }
                    }
                )
            },
            thumbModifier = Modifier
                .offset(x = thumbOffset)
                .pointerInput(true) {
                    detectTapGestures(onPress = { offset -> pressOffset = offset.x })
                }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStarted = { isDragging = true },
                    onDragStopped = {
                        rawOffset = valueToThumbOffset(value, width, steps)
                        isDragging = false
                    }
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
        Track(
            tickFractions = tickFractions,
            modifier = Modifier.fillMaxWidth()
        )
        Thumb(thumbModifier)
    }
}

private fun <T> snapOffsetToTick(
    minPx: Float,
    maxPx: Float,
    rawOffset: Float,
    steps: List<T>,
    tickFractions: List<Float>,
): T {
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

    return steps[minIndex]
}

private fun <T> valueToThumbOffset(value: T, width: Float, steps: List<T>): Float {
    val index = steps.indexOf(value)
    return width / (steps.size - 1) * index
}

@Composable
private fun Track(
    tickFractions: List<Float>,
    modifier: Modifier = Modifier,
) {
    val color = GBDiaryTheme.gbDiaryColors.border

    Canvas(modifier) {
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