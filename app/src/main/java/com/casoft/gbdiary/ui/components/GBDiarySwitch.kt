package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import kotlin.math.roundToInt

private val TrackWidth = 40.dp
private val ThumbDiameter = 20.dp
private val ThumbPathLength = TrackWidth - ThumbDiameter

private val ThumbRippleRadius = 24.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GBDiarySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val swipeableState = rememberSwipeableState(initialValue = checked)
    var internalChecked by remember { mutableStateOf(checked) }

    val minBound = 0f
    val maxBound = with(LocalDensity.current) { ThumbPathLength.toPx() }
    val isLight = GBDiaryTheme.colors.isLight

    LaunchedEffect(internalChecked) {
        if (swipeableState.currentValue != internalChecked) {
            swipeableState.animateTo(internalChecked)
        }
    }

    LaunchedEffect(checked) {
        if (swipeableState.isAnimationRunning.not()) {
            swipeableState.snapTo(checked)
        }
        internalChecked = checked
    }

    LaunchedEffect(swipeableState.currentValue) {
        swipeableState.currentValue.let {
            if (it != checked) {
                internalChecked = it
                onCheckedChange(it)
            }
        }
    }

    Box(
        modifier = modifier
            .toggleable(
                value = checked,
                onValueChange = {
                    internalChecked = it
                    onCheckedChange(it)
                },
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
            .swipeable(
                state = swipeableState,
                anchors = mapOf(minBound to false, maxBound to true),
                orientation = Orientation.Horizontal,
                enabled = enabled,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                resistance = null
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Track(checked = checked, isLight = isLight)
        Thumb(
            isLight = isLight,
            swipeableState = swipeableState,
            interactionSource = interactionSource
        )
    }
}

@Composable
private fun Track(checked: Boolean, isLight: Boolean) {
    val uncheckedPainter = painterResource(
        id = if (isLight) {
            R.drawable.switch_track_unchecked_light
        } else {
            R.drawable.switch_track_unchecked_dark
        }
    )
    val checkedPainter = painterResource(
        id = if (isLight) {
            R.drawable.switch_track_checked_light
        } else {
            R.drawable.switch_track_checked_dark
        }
    )

    Image(
        painter = if (checked) checkedPainter else uncheckedPainter,
        contentDescription = "스위치"
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Thumb(
    isLight: Boolean,
    swipeableState: SwipeableState<Boolean>,
    interactionSource: InteractionSource,
) {
    val painter = painterResource(
        id = if (isLight) {
            R.drawable.switch_thumb_light
        } else {
            R.drawable.switch_thumb_dark
        }
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = swipeableState.offset.value.roundToInt(),
                    y = 0
                )
            }
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = ThumbRippleRadius
                )
            )
    )
}