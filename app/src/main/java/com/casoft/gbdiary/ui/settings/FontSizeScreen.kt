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
import androidx.compose.ui.unit.sp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.ui.components.DiaryCard
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.TickSlider
import com.casoft.gbdiary.util.style
import java.time.LocalDate

@Composable
fun FontSizeScreen(
    viewModel: FontSizeViewModel,
    onBack: () -> Unit,
) {
    val fontSize by viewModel.fontSize.collectAsState()

    FontSizeScreen(
        fontSize = fontSize,
        onFontSizeChange = viewModel::setFontSize,
        onBack = onBack
    )
}

@Composable
private fun FontSizeScreen(
    fontSize: DiaryFontSize,
    onFontSizeChange: (DiaryFontSize) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AppBar(onBack)
        Preview(
            fontSize = fontSize,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp)
        )
        FontSizeSlider(
            value = fontSize,
            onValueChange = onFontSizeChange,
            modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp)
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

@Composable
private fun Preview(
    fontSize: DiaryFontSize,
    modifier: Modifier = Modifier,
) {
    DiaryCard(
        item = DiaryItem(
            date = LocalDate.now(),
            stickers = listOf(Sticker.SATISFACTION),
            content = "꼬박꼬박 일기 쓰는 습관 :)\n폰트 사이즈를 변경할 수 있어요"
        ),
        contentTextStyle = fontSize.style,
        modifier = modifier.height(200.dp)
    )
}

@Composable
private fun FontSizeSlider(
    value: DiaryFontSize,
    onValueChange: (DiaryFontSize) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "A",
            fontSize = 16.sp
        )
        TickSlider(
            value = value,
            onValueChange = onValueChange,
            steps = DiaryFontSize.values().toList(),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "A",
            fontSize = 24.sp
        )
    }
}