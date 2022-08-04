package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import java.util.*

internal val CellSize = 44.dp
internal val HorizontalSpaceBetweenCells = 3.dp

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CalendarScreen() {
    val state = rememberCalendarState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
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
                modifier = Modifier.fillMaxHeight()
            )
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
        fontSize = 20.sp
    )
}