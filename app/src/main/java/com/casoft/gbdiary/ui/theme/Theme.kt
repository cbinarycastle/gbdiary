package com.casoft.gbdiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.Theme

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

private val LightGBDiaryColors = GBDiaryColors(
    dimmingOverlay = LightDimmingOverlay,
    border = Light3,
    enabled = Dark1
)

private val DarkGBDiaryColors = GBDiaryColors(
    dimmingOverlay = DarkDimmingOverlay,
    border = Dark3,
    enabled = Light1
)

@Composable
fun GBDiaryTheme(
    theme: Theme,
    content: @Composable () -> Unit,
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val darkTheme = remember(theme, systemInDarkTheme) {
        when (theme) {
            Theme.SYSTEM -> systemInDarkTheme
            Theme.DARK -> true
            Theme.LIGHT -> false
        }
    }

    GBDiaryTheme(darkTheme) {
        content()
    }
}

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
        val contentColor = contentColorFor(colors.background)
        val textSelectionColors = TextSelectionColors(
            handleColor = contentColor,
            backgroundColor = contentColor.copy(alpha = 0.4f)
        )

        CompositionLocalProvider(
            LocalGBDiaryColors provides gbDiaryColors,
            LocalContentColor provides contentColor,
            LocalTextSelectionColors provides textSelectionColors,
            LocalElevationOverlay provides null
        ) {
            Surface(color = colors.background) {
                content()
            }
        }
    }
}

object GBDiaryTheme {

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

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
    val disabled: Float
        get() = 0.2f
}

@Stable
class GBDiaryColors(
    dimmingOverlay: Color,
    border: Color,
    enabled: Color,
) {

    var dimmingOverlay by mutableStateOf(dimmingOverlay)
        private set

    var border by mutableStateOf(border)
        private set

    var enabled by mutableStateOf(enabled)
        private set
}

private val LocalGBDiaryColors = staticCompositionLocalOf { LightGBDiaryColors }

@Composable
fun markerPainter(): Painter {
    return painterResource(
        if (GBDiaryTheme.colors.isLight) {
            R.drawable.marker_light
        } else {
            R.drawable.marker_dark
        }
    )
}