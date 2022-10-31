package com.casoft.gbdiary.ui

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun DiaryApp() {
    val viewModel = hiltViewModel<AppViewModel>()
    val systemUiController = rememberSystemUiController()

    val theme by viewModel.theme.collectAsState()
    var shouldShowSplashScreen by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
    }

    LaunchedEffect(true) {
        delay(1000)
        shouldShowSplashScreen = false
    }

    GBDiaryTheme(theme) {
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