package com.casoft.gbdiary.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

val DiaryFontSize.style: TextStyle
    @Composable
    get() {
        val fontSize = when (this) {
            DiaryFontSize.XXS -> -6
            DiaryFontSize.XS -> -4
            DiaryFontSize.S -> -2
            DiaryFontSize.M -> 0
            DiaryFontSize.L -> 2
            DiaryFontSize.XL -> 4
            DiaryFontSize.XXL -> 6
        }.let { (GBDiaryTheme.typography.body1.fontSize.value + it).sp }

        val lineHeight = when (this) {
            DiaryFontSize.XXS -> 4
            DiaryFontSize.XS -> 4
            DiaryFontSize.S -> 5
            DiaryFontSize.M -> 6
            DiaryFontSize.L -> 7
            DiaryFontSize.XL -> 8
            DiaryFontSize.XXL -> 9
        }.let { (fontSize.value + it).sp }

        return GBDiaryTheme.typography.body1.copy(
            fontSize = fontSize,
            lineHeight = lineHeight
        )
    }