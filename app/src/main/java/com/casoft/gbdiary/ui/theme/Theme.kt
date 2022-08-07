package com.casoft.gbdiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Light2,
    primaryVariant = Light2,
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
    primary = Dark2,
    primaryVariant = Dark2,
    secondary = DarkTextIcon,
    secondaryVariant = DarkTextIcon,
    background = Dark1,
    surface = Dark2,
    onPrimary = DarkTextIcon,
    onSecondary = Dark2,
    onBackground = DarkTextIcon,
    onSurface = DarkTextIcon
)

private val LightGBDiaryColors = GBDiaryColors(border = Light3)

private val DarkGBDiaryColors = GBDiaryColors(border = Dark3)

@Composable
fun GBDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val gbDiaryColors = if (darkTheme) DarkGBDiaryColors else LightGBDiaryColors

    MaterialTheme(
        colors = colors,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalGBDiaryColors provides gbDiaryColors,
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

    val gbDiaryColors: GBDiaryColors
        @Composable
        @ReadOnlyComposable
        get() = LocalGBDiaryColors.current
}

object GBDiaryContentAlpha {

    val dim: Float
        @Composable
        @ReadOnlyComposable
        get() = if (GBDiaryTheme.colors.isLight) 0.3f else 0.5f

    val disabled: Float
        get() = 0.2f
}

@Stable
class GBDiaryColors(border: Color) {
    var border by mutableStateOf(border)
        private set
}

private val LocalGBDiaryColors = staticCompositionLocalOf { LightGBDiaryColors }