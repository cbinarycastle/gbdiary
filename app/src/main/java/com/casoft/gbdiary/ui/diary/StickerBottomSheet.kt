package com.casoft.gbdiary.ui.diary

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.StickerType
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.components.AppBarHeight
import com.casoft.gbdiary.ui.extension.statusBarHeight
import com.casoft.gbdiary.ui.modifier.alignTopToCenterOfParent
import com.casoft.gbdiary.ui.modifier.noRippleClickable
import com.casoft.gbdiary.ui.theme.Dark1
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.markerPainter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val NUMBER_OF_STICKERS_BY_ROW = 3

private val DateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd EEEE")

@Composable
fun StickerBottomSheet(
    scrollState: ScrollState,
    date: LocalDate,
    isPremiumUser: Boolean,
    onStickerSelected: (Sticker) -> Unit,
) {
    var selectedStickerType by remember { mutableStateOf(StickerType.MOOD) }
    val stickerEnabled = remember(selectedStickerType, isPremiumUser) {
        selectedStickerType == StickerType.MOOD || isPremiumUser
    }

    LaunchedEffect(selectedStickerType) {
        scrollState.scrollTo(0)
    }

    BoxWithConstraints {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight - statusBarHeight - AppBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Spacer(
                modifier = Modifier
                    .width(40.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(GBDiaryTheme.gbDiaryColors.border)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = date.format(DateFormatter),
                style = GBDiaryTheme.typography.caption,
                modifier = Modifier.alpha(0.4f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "오늘은 어떤 하루였나요?",
                style = GBDiaryTheme.typography.subtitle1
            )
            Spacer(Modifier.height(24.dp))
            Row {
                StickerTypeTab(
                    text = StickerType.MOOD.text,
                    selected = selectedStickerType == StickerType.MOOD,
                    onClick = { selectedStickerType = StickerType.MOOD }
                )
                StickerTypeTab(
                    text = StickerType.DAILY.text,
                    selected = selectedStickerType == StickerType.DAILY,
                    onClick = { selectedStickerType = StickerType.DAILY }
                )
            }
            Spacer(Modifier.height(24.dp))
            Stickers(
                scrollState = scrollState,
                stickerType = selectedStickerType,
                onStickerClick = onStickerSelected,
                enabled = stickerEnabled
            )
        }
    }
}

@Composable
private fun StickerTypeTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(27.dp)
            .clickable { onClick() }
    ) {
        if (selected) {
            Image(
                painter = markerPainter(),
                contentDescription = null,
                modifier = Modifier.alignTopToCenterOfParent()
            )
        }
        Text(
            text = text,
            style = GBDiaryTheme.typography.body1,
            modifier = Modifier
                .align(Alignment.Center)
                .then(
                    if (selected) {
                        Modifier
                    } else {
                        Modifier.alpha(0.4f)
                    }
                )
        )
    }
}

@Composable
private fun Stickers(
    scrollState: ScrollState = rememberScrollState(),
    stickerType: StickerType,
    onStickerClick: (Sticker) -> Unit,
    enabled: Boolean = true,
) {
    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp)
        ) {
            Sticker.values()
                .filter { it.type == stickerType }
                .toList()
                .chunked(NUMBER_OF_STICKERS_BY_ROW)
                .forEach { stickers ->
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        stickers.forEach { sticker ->
                            Sticker(
                                sticker = sticker,
                                onClick = onStickerClick,
                                enabled = enabled
                            )
                        }
                    }
                }
        }
        if (enabled.not()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Dark1,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = "프리미엄 이용권 구매 후 사용할 수 있어요",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun Sticker(
    sticker: Sticker,
    onClick: (Sticker) -> Unit,
    enabled: Boolean = true,
) {
    Image(
        painter = painterResource(sticker.imageResId),
        contentDescription = sticker.name,
        modifier = Modifier
            .size(72.dp)
            .then(
                if (enabled) {
                    Modifier.noRippleClickable { onClick(sticker) }
                } else {
                    Modifier.alpha(0.3f)
                }
            )
    )
}