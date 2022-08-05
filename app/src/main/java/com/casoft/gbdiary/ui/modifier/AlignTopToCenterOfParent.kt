package com.casoft.gbdiary.ui.modifier

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout

fun Modifier.alignTopToCenterOfParent(
    alignment: Alignment.Horizontal = Alignment.CenterHorizontally,
) = layout { measurable, constraints ->

    val placeable = measurable.measure(constraints)
    val x = alignment.align(
        size = placeable.width,
        space = constraints.maxWidth,
        layoutDirection = layoutDirection
    )
    val y = constraints.maxHeight / 2

    layout(placeable.width, placeable.height) {
        placeable.place(x, y)
    }
}