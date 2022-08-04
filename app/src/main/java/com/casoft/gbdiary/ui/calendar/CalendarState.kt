package com.casoft.gbdiary.ui.calendar

import androidx.compose.runtime.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.ChronoUnit

internal const val DAYS_IN_WEEK = 7
internal const val PAGE_COUNT = 10000
internal const val INITIAL_PAGE = PAGE_COUNT / 2

@Stable
class CalendarState(private val initialYearMonth: YearMonth) {

    var currentYearMonth by mutableStateOf(initialYearMonth)
        private set

    internal fun getMonthByPage(page: Int, firstDayOfWeek: DayOfWeek): Month {
        val offsetFromInitialPage = INITIAL_PAGE.toLong() - page
        val yearMonth = initialYearMonth.minusMonths(offsetFromInitialPage)

        val dayOfWeeks = listOf(0 until DAYS_IN_WEEK).flatten()
            .map { firstDayOfWeek.plus(it.toLong()) }

        val weeks = getWeeks(yearMonth = yearMonth, firstDayOfWeek = firstDayOfWeek)

        return Month(yearMonth = yearMonth, dayOfWeeks = dayOfWeeks, weeks = weeks)
    }

    private fun getWeeks(yearMonth: YearMonth, firstDayOfWeek: DayOfWeek): List<Week> {
        val firstDayOfPage = getFirstDayOfPage(
            firstDayOfMonth = yearMonth.atDay(1),
            firstDayOfWeek = firstDayOfWeek
        )
        val lastDayOfPage = getLastDayOfPage(
            lastDayOfMonth = yearMonth.atEndOfMonth(),
            firstDayOfWeek = firstDayOfWeek
        )

        val daysOfPage = ChronoUnit.DAYS.between(firstDayOfPage, lastDayOfPage)
        return listOf(0..daysOfPage).flatten()
            .map {
                val date = firstDayOfPage.plusDays(it)
                Day(date = date, inCurrentMonth = YearMonth.from(date) == yearMonth)
            }
            .chunked(DAYS_IN_WEEK)
            .map { days -> Week(days) }
    }

    private fun getFirstDayOfPage(firstDayOfMonth: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
        var firstDayOfPage = firstDayOfMonth
        while (firstDayOfPage.dayOfWeek != firstDayOfWeek) {
            firstDayOfPage = firstDayOfPage.minusDays(1)
        }
        return firstDayOfPage
    }

    private fun getLastDayOfPage(lastDayOfMonth: LocalDate, firstDayOfWeek: DayOfWeek): LocalDate {
        var lastDayOfPage = lastDayOfMonth
        while (lastDayOfPage.dayOfWeek != firstDayOfWeek.minus(1)) {
            lastDayOfPage = lastDayOfPage.plusDays(1)
        }
        return lastDayOfPage
    }
}

@Composable
fun rememberCalendarState(
    initialYearMonth: YearMonth = YearMonth.now(),
): CalendarState = remember(initialYearMonth) {
    CalendarState(initialYearMonth = initialYearMonth)
}