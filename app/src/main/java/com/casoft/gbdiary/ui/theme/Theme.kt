package com.casoft.gbdiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val LightColors = lightColors(
    primary = Light1,
    primaryVariant = Light1,
    secondary = LightTextIcon,
    secondaryVariant = LightTextIcon,
    background = Light1,
    surface = Light2,
    onPrimary = LightTextIcon,
    onSecondary = Light2,
    onBackground = LightTextIcon,
    onSurface = LightTextIcon
)

private val DarkColors = darkColors(
    primary = Dark1,
    primaryVariant = Dark1,
    secondary = DarkTextIcon,
    secondaryVariant = DarkTextIcon,
    background = Dark1,
    surface = Dark2,
    onPrimary = DarkTextIcon,
    onSecondary = Dark2,
    onBackground = DarkTextIcon,
    onSurface = DarkTextIcon
)

@Composable
fun GBDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colors = colors,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColorFor(colors.background)
        ) {
            content()
        }
    }
}

object GBDiaryTheme {
    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colors
}

val ContentAlpha.dim: Float
    @Composable
    @ReadOnlyComposable
    get() = if (GBDiaryTheme.colors.isLight) 0.3f else 0.5f

val ContentAlpha.disabledText: Float
    get() = 0.2f