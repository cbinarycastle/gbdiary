package com.casoft.gbdiary.ui.statistics

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.model.Statistics
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.ui.components.AlertDialogLayout
import com.casoft.gbdiary.ui.components.AlertDialogState
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.rememberAlertDialogState
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.collectMessage
import com.casoft.gbdiary.util.color
import com.casoft.gbdiary.util.imageResId
import java.time.YearMonth

private const val MAX_TOP_STICKERS = 3

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    navigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val alertDialogState = rememberAlertDialogState()

    val statistics by viewModel.statistics.collectAsState()
    val hasNextMonth by viewModel.hasNextMonth.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.message.collectMessage(context, alertDialogState)
    }

    StatisticsScreen(
        alertDialogState = alertDialogState,
        statistics = statistics,
        hasNextMonth = hasNextMonth,
        navigateUp = navigateUp,
        moveToPreviousMonth = viewModel::moveToPreviousMonth,
        moveToNextMonth = viewModel::moveToNextMonth
    )
}

@Composable
private fun StatisticsScreen(
    alertDialogState: AlertDialogState,
    statistics: Statistics,
    hasNextMonth: Boolean,
    navigateUp: () -> Unit,
    moveToPreviousMonth: () -> Unit,
    moveToNextMonth: () -> Unit,
) {
    val stickerCounts = statistics.stickerCounts

    AlertDialogLayout(state = alertDialogState) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.systemBarsPadding()
        ) {
            AppBar(navigateUp)
            Spacer(Modifier.height(8.dp))
            YearMonthHeader(
                yearMonth = statistics.yearMonth,
                hasNextMonth = hasNextMonth,
                moveToPreviousMonth = moveToPreviousMonth,
                moveToNextMonth = moveToNextMonth
            )
            Spacer(Modifier.height(24.dp))
            TopStickers(stickerCounts)
            Spacer(Modifier.height(32.dp))
            StatisticsGraph(
                stickerCounts = stickerCounts,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun AppBar(navigateUp: () -> Unit) {
    GBDiaryAppBar {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = navigateUp) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "닫기"
                )
            }
        }
    }
}

@Composable
private fun YearMonthHeader(
    yearMonth: YearMonth,
    hasNextMonth: Boolean,
    moveToPreviousMonth: () -> Unit,
    moveToNextMonth: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = moveToPreviousMonth) {
            Icon(
                painter = painterResource(R.drawable.chevron_left),
                contentDescription = "이전"
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CompositionLocalProvider(LocalContentAlpha provides 0.5f) {
                Text(
                    text = yearMonth.year.toString(),
                    style = GBDiaryTheme.typography.subtitle1
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${yearMonth.monthValue}월",
                style = GBDiaryTheme.typography.h5
            )
        }
        IconButton(
            onClick = moveToNextMonth,
            enabled = hasNextMonth
        ) {
            if (hasNextMonth) {
                Icon(
                    painter = painterResource(R.drawable.chevron_right),
                    contentDescription = "다음"
                )
            }
        }
    }
}

@Composable
private fun TopStickers(stickerCounts: List<Statistics.StickerCount>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(64.dp)
    ) {
        stickerCounts.take(MAX_TOP_STICKERS).forEach { stickerCount ->
            val sticker = stickerCount.sticker
            Image(
                painter = painterResource(sticker.imageResId),
                contentDescription = sticker.name,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
private fun StatisticsGraph(
    stickerCounts: List<Statistics.StickerCount>,
    modifier: Modifier = Modifier,
) {
    Surface(modifier.fillMaxWidth()) {
        if (stickerCounts.isEmpty()) {
            Column(Modifier.fillMaxSize()) {
                Spacer(Modifier.fillMaxHeight(0.3f))
                CompositionLocalProvider(LocalContentAlpha provides 0.3f) {
                    Text(
                        text = "추가한 스티커가 없어요",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Spacer(Modifier.fillMaxHeight(0.7f))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stickerCounts) { stickerCount ->
                    StatisticsGraphItem(
                        stickerCount = stickerCount,
                        maxCount = stickerCounts.maxOf { it.count }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticsGraphItem(
    stickerCount: Statistics.StickerCount,
    maxCount: Int,
) {
    val sticker = stickerCount.sticker
    val count = stickerCount.count

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(sticker.imageResId),
            contentDescription = sticker.name,
            modifier = Modifier.size(48.dp)
        )
        StatisticsGraphBar(
            value = count,
            max = maxCount,
            color = sticker.color,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = count.toString(),
            modifier = Modifier.width(20.dp)
        )
    }
}

@Composable
private fun StatisticsGraphBar(
    value: Int,
    max: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(GBDiaryTheme.gbDiaryColors.border)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(value.toFloat() / max.toFloat())
                .height(8.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Preview(name = "Statistics screen")
@Preview(name = "Statistics screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsScreenPreview() {
    GBDiaryTheme {
        StatisticsScreen(
            alertDialogState = rememberAlertDialogState(),
            statistics = Statistics(
                yearMonth = YearMonth.of(2023, 3),
                stickerCounts = listOf(
                    Statistics.StickerCount(Sticker.TIRED, 10),
                    Statistics.StickerCount(Sticker.HOPEFUL, 5),
                    Statistics.StickerCount(Sticker.IMPASSIVE, 4),
                    Statistics.StickerCount(Sticker.JOY, 2),
                    Statistics.StickerCount(Sticker.SLEEPINESS, 1),
                    Statistics.StickerCount(Sticker.COFFEE, 1),
                    Statistics.StickerCount(Sticker.BIRTHDAY_CAKE, 1),
                    Statistics.StickerCount(Sticker.MOVIE, 1),
                    Statistics.StickerCount(Sticker.SUNNY, 1)
                )
            ),
            hasNextMonth = false,
            navigateUp = {},
            moveToPreviousMonth = {},
            moveToNextMonth = {}
        )
    }
}