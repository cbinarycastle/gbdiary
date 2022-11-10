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
fun DiaryApp(finishActivity: () -> Unit) {
    val viewModel = hiltViewModel<AppViewModel>()
    val systemUiController = rememberSystemUiController()

    val theme by viewModel.theme.collectAsState()

    var showSplashScreen by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
    }

    val screenLockEnabled by viewModel.screenLockEnabled.collectAsState()

    LaunchedEffect(true) {
        delay(1000)
        showSplashScreen = false
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
            GBDiaryNavGraph(
                screenLockEnabled = screenLockEnabled,
                finishActivity = finishActivity
            )
            if (showSplashScreen) {
                SplashScreen()
            }
        }
    }
}