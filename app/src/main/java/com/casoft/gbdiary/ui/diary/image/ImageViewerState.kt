package com.casoft.gbdiary.ui.diary.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun rememberImageViewerState(
    systemUiController: SystemUiController = rememberSystemUiController(),
    useDarkIcons: Boolean = GBDiaryTheme.colors.isLight,
) = remember(systemUiController, useDarkIcons) {
    ImageViewerState(systemUiController, useDarkIcons)
}

@Stable
class ImageViewerState(
    private val systemUiController: SystemUiController,
    private val useDarkIcons: Boolean,
) {
    init {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }

    fun onClose() {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
}