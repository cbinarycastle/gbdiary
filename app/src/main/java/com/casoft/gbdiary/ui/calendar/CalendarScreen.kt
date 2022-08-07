package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.extensions.yearMonth
import com.casoft.gbdiary.ui.GBDiaryAppBar
import com.casoft.gbdiary.ui.theme.ImHyemin
import com.google.accompanist.pager.ExperimentalPagerApi
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import java.util.*

internal val HorizontalSpaceBetweenCells = 3.dp

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CalendarScreen(onSettingsClick: () -> Unit) {
    val state = rememberCalendarState()
    val today = LocalDate.now()

    Box {
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
                    CalendarHeader(yearMonth = state.currentYearMonth)
                    Spacer(Modifier.height(32.dp))
                    WeekHeader(Modifier.align(Alignment.CenterHorizontally))
                    Calendar(
                        modifier = Modifier
                            .height(314.dp)
                            .align(Alignment.CenterHorizontally),
                        state = state,
                    ) { month ->
                        Month(
                            month = month,
                            today = today,
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
private fun CalendarHeader(yearMonth: YearMonth) {
    Text(
        text = yearMonth.month.getDisplayName(
            TextStyle.FULL,
            Locale.getDefault()
        ),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = ImHyemin
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
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(
            start = 14.dp,
            top = 4.dp,
            end = 14.dp,
            bottom = 8.dp
        )
    ) {
        Text(
            text = "오늘",
            fontSize = 20.sp
        )
    }
}

@Composable
private fun WriteButton(modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = { /*TODO*/ },
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.edit),
            contentDescription = "작성"
        )
    }
}