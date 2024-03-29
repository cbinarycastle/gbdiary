package com.casoft.gbdiary.ui.calendar

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ad.MAIN_BANNER_AD_UNIT_ID
import com.casoft.gbdiary.ui.components.AD_HEIGHT
import com.casoft.gbdiary.ui.components.AdBanner
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.MonthPickerDialog
import com.casoft.gbdiary.ui.theme.GBDiaryContentAlpha
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.findActivity
import com.casoft.gbdiary.util.yearMonth
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.android.play.core.ktx.launchReview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

internal val HorizontalSpaceBetweenCells = 3.dp

private const val PagesFromToday = (PAGE_COUNT / 2 - 1).toLong()

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onDayClick: (LocalDate) -> Unit,
    onTimelineClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWriteClick: (LocalDate) -> Unit,
    state: CalendarState = rememberCalendarState(),
    today: LocalDate = LocalDate.now(),
) {
    val context = LocalContext.current

    val isPremiumUser by viewModel.isPremiumUser.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.reviewInfo.collect { reviewInfo ->
            if (reviewInfo != null) {
                viewModel.run {
                    reviewManager.launchReview(
                        activity = context.findActivity(),
                        reviewInfo = reviewInfo
                    )
                    onLaunchReviewFlow()
                }
            }
        }
    }

    LaunchedEffect(state.currentYearMonth) {
        viewModel.currentYearMonth = state.currentYearMonth
    }

    CalendarScreen(
        state = state,
        today = today,
        isPremiumUser = isPremiumUser,
        getDayStateList = { yearMonth -> viewModel.getDayStateList(yearMonth) },
        onDayClick = onDayClick,
        onTimelineClick = onTimelineClick,
        onStatisticsClick = onStatisticsClick,
        onSearchClick = onSearchClick,
        onSettingsClick = onSettingsClick,
        onWriteClick = onWriteClick
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CalendarScreen(
    state: CalendarState,
    today: LocalDate,
    isPremiumUser: Boolean,
    getDayStateList: (YearMonth) -> StateFlow<DayStateList>,
    onDayClick: (LocalDate) -> Unit,
    onTimelineClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWriteClick: (LocalDate) -> Unit,
) {
    var showMonthPickerDialog by remember { mutableStateOf(false) }

    Box(Modifier.systemBarsPadding()) {
        Column(Modifier.fillMaxSize()) {
            AppBar(
                onTimelineClick = onTimelineClick,
                onStatisticsClick = onStatisticsClick,
                onSearchClick = onSearchClick,
                onSettingsClick = onSettingsClick
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(top = 80.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    MonthHeader(
                        yearMonth = state.currentYearMonth,
                        onClick = { showMonthPickerDialog = true }
                    )
                    Spacer(Modifier.height(32.dp))
                    WeekHeader(Modifier.align(Alignment.CenterHorizontally))
                    Calendar(
                        modifier = Modifier
                            .wrapContentWidth()
                            .aspectRatio(314f / 327f)
                            .align(Alignment.CenterHorizontally),
                        state = state,
                    ) { month ->
                        val dayStateList by getDayStateList(month.yearMonth).collectAsState()
                        Month(
                            month = month,
                            dayStateList = dayStateList,
                            today = today,
                            onDayClick = onDayClick,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = AD_HEIGHT.dp + 8.dp)
        ) {
            if (state.currentYearMonth != today.yearMonth) {
                TodayButton(
                    onClick = { state.currentYearMonth = today.yearMonth },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            WriteButton(
                onClick = { onWriteClick(today) },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
            )
        }
        if (isPremiumUser.not()) {
            AdBanner(
                adUnitId = MAIN_BANNER_AD_UNIT_ID,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showMonthPickerDialog) {
            MonthPickerDialog(
                initialYear = state.currentYearMonth.year,
                today = today.yearMonth,
                isEnabled = { yearMonth ->
                    val minYearMonth = today.yearMonth.minusMonths(PagesFromToday)
                    val maxYearMonth = today.yearMonth.plusMonths(PagesFromToday)
                    yearMonth >= minYearMonth || yearMonth <= maxYearMonth
                },
                onSelect = { yearMonth ->
                    showMonthPickerDialog = false
                    state.currentYearMonth = yearMonth
                },
                onDismiss = { showMonthPickerDialog = false }
            )
        }
    }
}

@Composable
private fun AppBar(
    onTimelineClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    GBDiaryAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box {
                IconButton(onClick = onTimelineClick) {
                    Icon(
                        painter = painterResource(R.drawable.timeline),
                        contentDescription = "타임라인"
                    )
                }
                IconButton(
                    onClick = onStatisticsClick,
                    modifier = Modifier.padding(start = 36.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.statistics),
                        contentDescription = "통계"
                    )
                }
            }
            Box(contentAlignment = Alignment.CenterEnd) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.padding(end = 36.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = "검색"
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        painter = painterResource(R.drawable.setting),
                        contentDescription = "설정"
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    yearMonth: YearMonth,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = yearMonth.month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            ),
            style = GBDiaryTheme.typography.h5
        )
        CompositionLocalProvider(LocalContentAlpha provides GBDiaryContentAlpha.disabled) {
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = "월 이동"
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TodayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        elevation = 1.dp,
        modifier = modifier
    ) {
        Box(Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Text(
                text = "오늘",
                style = GBDiaryTheme.typography.body2
            )
        }
    }
}

@Composable
private fun WriteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.edit),
            contentDescription = "작성"
        )
    }
}

@Preview(name = "Calendar screen")
@Preview(name = "Calendar screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CalendarScreenPreview() {
    GBDiaryTheme {
        CalendarScreen(
            state = rememberCalendarState(initialYearMonth = YearMonth.of(2022, 1)),
            today = LocalDate.of(2022, 1, 1),
            isPremiumUser = false,
            getDayStateList = { MutableStateFlow(DayStateList.empty()) },
            onDayClick = {},
            onTimelineClick = {},
            onStatisticsClick = {},
            onSearchClick = {},
            onSettingsClick = {},
            onWriteClick = {}
        )
    }
}