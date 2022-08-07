package com.casoft.gbdiary.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

@Composable
fun DiaryApp() {
    GBDiaryTheme {
        Surface(color = GBDiaryTheme.colors.background) {
            GBDiaryNavGraph()
        }
    }
}