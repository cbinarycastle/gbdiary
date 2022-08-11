package com.casoft.gbdiary.ui.extension

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

val statusBarHeight: Dp
    @Composable get() = with(LocalDensity.current) {
        WindowInsets.statusBars.getTop(this).toDp()
    }