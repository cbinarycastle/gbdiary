package com.casoft.gbdiary.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

@Composable
fun GBDiaryRadioButton(
    selected: Boolean,
    onClick: (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val selectedColor = GBDiaryTheme.colors.onBackground
    val unselectedColor = GBDiaryTheme.gbDiaryColors.border
    val dotColor = if (GBDiaryTheme.colors.isLight) {
        GBDiaryTheme.colors.primary
    } else {
        GBDiaryTheme.colors.background
    }

    val dotRadius by animateDpAsState(
        targetValue = if (selected) RadioButtonDotSize / 2 else 0.dp,
        animationSpec = tween(durationMillis = RadioAnimationDuration)
    )

    Canvas(
        modifier = modifier
            .size(LocalViewConfiguration.current.minimumTouchTargetSize)
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = RadioButtonRippleRadius
                )
            )
    ) {
        if (selected) {
            drawCircle(
                color = selectedColor,
                radius = RadioRadius.toPx()
            )
        } else {
            val strokeWidth = RadioStrokeWidth.toPx()
            drawCircle(
                color = unselectedColor,
                style = Stroke(strokeWidth),
                radius = RadioRadius.toPx() - strokeWidth / 2
            )
        }
        drawCircle(
            color = dotColor,
            radius = dotRadius.toPx()
        )
    }
}

private const val RadioAnimationDuration = 100

private val RadioButtonRippleRadius = 24.dp

private val RadioButtonSize = 20.dp
private val RadioRadius = RadioButtonSize / 2
private val RadioButtonDotSize = 8.dp
private val RadioStrokeWidth = 1.8.dp