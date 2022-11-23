package com.casoft.gbdiary.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

val DiaryFontSize.sp: TextUnit
    @Composable
    get() {
        val value = GBDiaryTheme.typography.body1.fontSize.value + when (this) {
            DiaryFontSize.XXS -> -6
            DiaryFontSize.XS -> -4
            DiaryFontSize.S -> -2
            DiaryFontSize.M -> 0
            DiaryFontSize.L -> 2
            DiaryFontSize.XL -> 4
            DiaryFontSize.XXL -> 6
        }
        return value.sp
    }