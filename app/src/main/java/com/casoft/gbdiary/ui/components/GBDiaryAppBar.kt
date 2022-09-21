package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
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
    showDivider: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    Column {
        TopAppBar(
            modifier = modifier,
            backgroundColor = backgroundColor,
            elevation = 0.dp
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                ProvideTextStyle(GBDiaryTheme.typography.subtitle1) {
                    content()
                }
            }
        }
        if (showDivider) {
            Divider(color = GBDiaryTheme.colors.onSurface.copy(alpha = 0.05f))
        }
    }
}