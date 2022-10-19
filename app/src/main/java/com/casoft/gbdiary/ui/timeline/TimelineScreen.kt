package com.casoft.gbdiary.ui.timeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ad.TIMELINE_BANNER_AD_UNIT_ID
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.ui.components.AdBanner
import com.casoft.gbdiary.ui.components.DiaryCard
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.MonthPickerDialog
import com.casoft.gbdiary.ui.modifier.noRippleClickable
import java.time.YearMonth

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel,
    onDiaryItemClick: (DiaryItem) -> Unit,
    onBack: () -> Unit,
    today: YearMonth = YearMonth.now(),
) {
    val yearMonth by viewModel.yearMonth.collectAsState()
    val diaryItems by viewModel.diaryItems.collectAsState()
    val isPremiumUser by viewModel.isPremiumUser.collectAsState()

    TimelineScreen(
        yearMonth = yearMonth,
        today = today,
        diaryItems = diaryItems,
        isPremiumUser = isPremiumUser,
        moveToYearMonth = viewModel::moveToYearMonth,
        onBeforeMonth = viewModel::moveToBeforeMonth,
        onNextMonth = viewModel::moveToNextMonth,
        onDiaryItemClick = onDiaryItemClick,
        onBack = onBack
    )
}

@Composable
private fun TimelineScreen(
    yearMonth: YearMonth,
    today: YearMonth,
    diaryItems: List<DiaryItem>,
    isPremiumUser: Boolean,
    moveToYearMonth: (YearMonth) -> Unit,
    onBeforeMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDiaryItemClick: (DiaryItem) -> Unit,
    onBack: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    var showMonthPickerDialog by remember { mutableStateOf(false) }

    val shouldShowAppBarDivider by remember {
        derivedStateOf { lazyListState.firstVisibleItemScrollOffset > 0 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            AppBar(
                yearMonth = yearMonth,
                showDivider = shouldShowAppBarDivider,
                onYearMonthClick = { showMonthPickerDialog = true },
                onBefore = onBeforeMonth,
                onNext = onNextMonth,
                onBack = onBack
            )
            DiaryCards(
                lazyListState = lazyListState,
                items = diaryItems,
                onItemClick = onDiaryItemClick,
                modifier = Modifier.weight(1f)
            )
            if (isPremiumUser.not()) {
                AdBanner(TIMELINE_BANNER_AD_UNIT_ID)
            }
        }

        if (diaryItems.isEmpty()) {
            Text(
                text = "작성된 일기가 없어요",
                modifier = Modifier
                    .align(Alignment.Center)
                    .alpha(0.3f)
            )
        }

        if (showMonthPickerDialog) {
            MonthPickerDialog(
                initialYear = yearMonth.year,
                today = today,
                isEnabled = { it <= today },
                onSelect = {
                    showMonthPickerDialog = false
                    moveToYearMonth(it)
                },
                onDismiss = { showMonthPickerDialog = false }
            )
        }
    }
}

@Composable
private fun AppBar(
    yearMonth: YearMonth,
    showDivider: Boolean,
    onYearMonthClick: () -> Unit,
    onBefore: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    GBDiaryAppBar(showDivider = showDivider) {
        Box(Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "뒤로"
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Center)
            ) {
                IconButton(onClick = onBefore) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_circle_left),
                        contentDescription = "이전"
                    )
                }
                Text(
                    text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
                    modifier = Modifier.noRippleClickable { onYearMonthClick() }
                )
                IconButton(onClick = onNext) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_circle_right),
                        contentDescription = "다음"
                    )
                }
            }
        }
    }
}

@Composable
private fun DiaryCards(
    lazyListState: LazyListState,
    items: List<DiaryItem>,
    onItemClick: (DiaryItem) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(items) { item ->
            DiaryCard(
                item = item,
                onClick = { onItemClick(item) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}