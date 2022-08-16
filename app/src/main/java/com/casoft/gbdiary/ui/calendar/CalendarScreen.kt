package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.GBDiaryAppBar
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.yearMonth
import com.google.accompanist.pager.ExperimentalPagerApi
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import java.util.*

internal val HorizontalSpaceBetweenCells = 3.dp

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onDayClick: (LocalDate) -> Unit,
    onSettingsClick: () -> Unit,
    onWriteClick: (LocalDate) -> Unit,
) {
    val state = rememberCalendarState()
    val today = LocalDate.now()

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
                        val stickers by viewModel.getStickers(month.yearMonth)
                            .collectAsState(mapOf())
                        Month(
                            month = month,
                            today = today,
                            modifier = Modifier.fillMaxHeight(),
                            stickers = stickers,
                            onDayClick = onDayClick,
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