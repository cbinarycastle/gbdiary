package com.casoft.gbdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.ThemeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DiaryApp() {
    val themeViewModel = hiltViewModel<ThemeViewModel>()
    val systemUiController = rememberSystemUiController()

    GBDiaryTheme(themeViewModel) {
        val useDarkIcons = GBDiaryTheme.colors.isLight

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }

        GBDiaryNavGraph()
    }
}