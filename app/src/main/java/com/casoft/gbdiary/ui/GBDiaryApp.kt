package com.casoft.gbdiary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.ThemeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun DiaryApp() {
    val themeViewModel = hiltViewModel<ThemeViewModel>()
    val systemUiController = rememberSystemUiController()

    var shouldShowSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        delay(1000)
        shouldShowSplashScreen = false
    }

    GBDiaryTheme(themeViewModel) {
        val useDarkIcons = GBDiaryTheme.colors.isLight

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }

        Box(Modifier.fillMaxSize()) {
            GBDiaryNavGraph()
            if (shouldShowSplashScreen) {
                SplashScreen()
            }
        }
    }
}