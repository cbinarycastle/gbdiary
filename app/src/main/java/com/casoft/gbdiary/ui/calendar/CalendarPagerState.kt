package com.casoft.gbdiary.ui.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.DayOfWeek
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.ChronoUnit

internal const val PAGE_COUNT = 10000
internal const val INITIAL_PAGE = PAGE_COUNT / 2

@OptIn(ExperimentalPagerApi::class)
@Stable
class CalendarPagerState constructor(
    private val calendarState: CalendarState,
    private val pagerState: PagerState,
    coroutineScope: CoroutineScope,
) {
    init {
        snapshotFlow { calendarState.currentYearMonth }
            .distinctUntilChanged()
            .onEach {
                val newPage = getPageFromYearMonth(it)
                if (pagerState.currentPage != newPage) {
                    pagerState.scrollToPage(newPage)
                }
            }
            .launchIn(coroutineScope)

        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .onEach {
                val newYearMonth = getYearMonthByPage(page = it)
                if (calendarState.currentYearMonth != newYearMonth) {
                    calendarState.currentYearMonth = newYearMonth
                }
            }
            .launchIn(coroutineScope)
    }

    internal fun getMonthByPage(page: Int, firstDayOfWeek: DayOfWeek): Month {
        val yearMonth = getYearMonthByPage(page)
        val dayOfWeeks = listOf(0 until DAYS_IN_WEEK).flatten()
            .map { firstDayOfWeek.plus(it.toLong()) }
        val weeks = calendarState.getWeeks(yearMonth = yearMonth, firstDayOfWeek = firstDayOfWeek)

        return Month(yearMonth = yearMonth, dayOfWeeks = dayOfWeeks, weeks = weeks)
    }

    private fun getYearMonthByPage(page: Int): YearMonth {
        val offsetFromInitialPage = INITIAL_PAGE.toLong() - page
        return calendarState.initialYearMonth.minusMonths(offsetFromInitialPage)
    }

    private fun getPageFromYearMonth(yearMonth: YearMonth): Int {
        val offsetFromInitialYearMonth = ChronoUnit.MONTHS.between(
            calendarState.initialYearMonth,
            yearMonth
        ).toInt()
        return INITIAL_PAGE + offsetFromInitialYearMonth
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun rememberCalendarPagerState(
    calendarState: CalendarState,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
): CalendarPagerState = remember(calendarState, pagerState, coroutineScope) {
    CalendarPagerState(
        calendarState = calendarState,
        pagerState = pagerState,
        coroutineScope = coroutineScope
    )
}