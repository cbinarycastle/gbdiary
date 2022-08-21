package com.casoft.gbdiary.ui.calendar

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.MonthPickerDialog
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.yearMonth
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import java.util.*

internal val HorizontalSpaceBetweenCells = 3.dp

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onDayClick: (LocalDate) -> Unit,
    onTimelineClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWriteClick: (LocalDate) -> Unit,
    state: CalendarState = rememberCalendarState(),
    today: LocalDate = LocalDate.now(),
) {
    LaunchedEffect(state.currentYearMonth) {
        viewModel.currentYearMonth = state.currentYearMonth
    }

    CalendarScreen(
        state = state,
        today = today,
        getDayStateList = { yearMonth -> viewModel.getDayStateList(yearMonth) },
        onDayClick = onDayClick,
        onTimelineClick = onTimelineClick,
        onSettingsClick = onSettingsClick,
        onWriteClick = onWriteClick
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CalendarScreen(
    state: CalendarState,
    today: LocalDate,
    getDayStateList: (YearMonth) -> Flow<DayStateList>,
    onDayClick: (LocalDate) -> Unit,
    onTimelineClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onWriteClick: (LocalDate) -> Unit,
) {
    var showMonthPickerDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            AppBar(
                onTimelineClick = onTimelineClick,
                onSettingsClick = onSettingsClick,
                onCalendarClick = { showMonthPickerDialog = true }
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
                    MonthHeader(yearMonth = state.currentYearMonth)
                    Spacer(Modifier.height(32.dp))
                    WeekHeader(Modifier.align(Alignment.CenterHorizontally))
                    Calendar(
                        modifier = Modifier
                            .height(314.dp)
                            .align(Alignment.CenterHorizontally),
                        state = state,
                    ) { month ->
                        val dayStateList by getDayStateList(month.yearMonth)
                            .collectAsState(DayStateList.empty())
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
        if (state.currentYearMonth != today.yearMonth) {
            TodayButton(
                onClick = { state.currentYearMonth = today.yearMonth },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 34.dp)
            )
        }
        WriteButton(
            onClick = { onWriteClick(today) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 34.dp)
        )
        if (showMonthPickerDialog) {
            MonthPickerDialog(
                initialYear = state.currentYearMonth.year,
                today = today.yearMonth,
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
    onSettingsClick: () -> Unit,
    onCalendarClick: () -> Unit,
) {
    GBDiaryAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                IconButton(onClick = onTimelineClick) {
                    Icon(
                        painter = painterResource(R.drawable.timeline),
                        contentDescription = "타임라인"
                    )
                }
                IconButton(onClick = onCalendarClick) {
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = "월 이동"
                    )
                }
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

@Composable
private fun MonthHeader(yearMonth: YearMonth) {
    Text(
        text = yearMonth.month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        ),
        style = GBDiaryTheme.typography.h6,
        fontWeight = FontWeight.Bold
    )
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
            getDayStateList = { flowOf(DayStateList.empty()) },
            onTimelineClick = {},
            onDayClick = {},
            onSettingsClick = {},
            onWriteClick = {}
        )
    }
}