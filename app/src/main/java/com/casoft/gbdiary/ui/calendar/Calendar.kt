package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import org.threeten.bp.DayOfWeek

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    state: CalendarState = rememberCalendarState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable PagerScope.(Month) -> Unit,
) {
    VerticalPager(
        count = PAGE_COUNT,
        modifier = modifier,
        state = rememberPagerState(initialPage = INITIAL_PAGE),
        contentPadding = contentPadding
    ) { page ->
        val month = state.getMonthByPage(page, DayOfWeek.SUNDAY)
        content(month)
    }
}