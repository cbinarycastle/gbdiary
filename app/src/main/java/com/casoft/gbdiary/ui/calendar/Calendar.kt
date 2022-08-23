package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import java.time.DayOfWeek

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    state: CalendarState = rememberCalendarState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable PagerScope.(Month) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = INITIAL_PAGE)
    val calendarPagerState = rememberCalendarPagerState(
        calendarState = state,
        pagerState = pagerState,
        coroutineScope = rememberCoroutineScope()
    )

    VerticalPager(
        count = PAGE_COUNT,
        modifier = modifier,
        state = pagerState,
        contentPadding = contentPadding
    ) { page ->
        val month = calendarPagerState.getMonthByPage(page, DayOfWeek.SUNDAY)
        content(month)
    }
}