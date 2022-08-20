package com.casoft.gbdiary.ui.calendar

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.GBDiaryAppBar
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
    onSettingsClick: () -> Unit,
    onWriteClick: (LocalDate) -> Unit,
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            AppBar(onSettingsClick = onSettingsClick)
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
    }
}

@Composable
private fun AppBar(onSettingsClick: () -> Unit) {
    GBDiaryAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(R.drawable.timeline),
                        contentDescription = "타임라인"
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = "캘린더"
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

@Composable
private fun TodayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        contentPadding = PaddingValues(horizontal = 14.dp)
    ) {
        Text(
            text = "오늘",
            style = GBDiaryTheme.typography.body2
        )
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
            onDayClick = {},
            onSettingsClick = {},
            onWriteClick = {}
        )
    }
}