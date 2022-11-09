package com.casoft.gbdiary.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.casoft.gbdiary.R

val DearJsOfNote = FontFamily(Font(R.font.dear_jsofnote))

val Typography = Typography(
    defaultFontFamily = DearJsOfNote,
    h3 = TextStyle(fontSize = 28.sp),
    h4 = TextStyle(fontSize = 26.sp),
    h5 = TextStyle(fontSize = 24.sp),
    h6 = TextStyle(fontSize = 20.sp),
    subtitle1 = TextStyle(fontSize = 17.sp),
    body1 = TextStyle(fontSize = 16.sp),
    body2 = TextStyle(fontSize = 15.sp),
    caption = TextStyle(fontSize = 14.sp),
)