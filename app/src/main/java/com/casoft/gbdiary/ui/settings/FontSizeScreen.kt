package com.casoft.gbdiary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.ui.components.DiaryCard
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import java.time.LocalDate

@Composable
fun FontSizeScreen(
    viewModel: FontSizeViewModel,
    onBack: () -> Unit,
) {
    val diaryFontSize by viewModel.diaryFontSize.collectAsState()

    FontSizeScreen(
        diaryFontSize = diaryFontSize,
        onBack = onBack
    )
}

@Composable
private fun FontSizeScreen(
    diaryFontSize: DiaryFontSize,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AppBar(onBack)
        DiaryCard(
            item = DiaryItem(
                date = LocalDate.now(),
                stickers = listOf(Sticker.SATISFACTION),
                content = "꼬박꼬박 일기 쓰는 습관 :)\n폰트 사이즈를 변경할 수 있어요"
            ),
            onClick = {},
            contentFontSize = diaryFontSize,
            modifier = Modifier.heightIn(min = 200.dp)
        )
    }
}

@Composable
private fun AppBar(onBack: () -> Unit) {
    GBDiaryAppBar {
        Box(Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "뒤로"
                )
            }
            Text(
                text = Settings.FONT_SIZE.text,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}