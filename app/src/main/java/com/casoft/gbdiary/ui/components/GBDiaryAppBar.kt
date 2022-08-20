package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

val AppBarHeight = 56.dp

@Composable
fun GBDiaryAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = GBDiaryTheme.colors.background,
    content: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        elevation = 0.dp
    ) {
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.high,
            LocalTextStyle provides GBDiaryTheme.typography.subtitle1
        ) {
            content()
        }
    }
}