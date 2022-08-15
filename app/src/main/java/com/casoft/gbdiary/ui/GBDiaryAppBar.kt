package com.casoft.gbdiary.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

val AppBarHeight = 56.dp

@Composable
fun GBDiaryAppBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = GBDiaryTheme.colors.background,
        elevation = 0.dp
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            content()
        }
    }
}