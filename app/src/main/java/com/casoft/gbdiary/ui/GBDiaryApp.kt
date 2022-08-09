package com.casoft.gbdiary.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DiaryApp() {
    val systemUiController = rememberSystemUiController()

    GBDiaryTheme {
        val useDarkIcons = GBDiaryTheme.colors.isLight

        SideEffect {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }

        Surface(color = GBDiaryTheme.colors.background) {
            GBDiaryNavGraph()
        }
    }
}