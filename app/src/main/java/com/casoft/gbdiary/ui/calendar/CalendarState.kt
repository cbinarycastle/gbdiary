package com.casoft.gbdiary.ui.calendar

import androidx.compose.runtime.*
import com.casoft.gbdiary.util.yearMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

internal const val DAYS_IN_WEEK = 7

@Stable
class CalendarState(internal val initialYearMonth: YearMonth) {

    var currentYearMonth by mutableStateOf(initialYearMonth)
        internal set

    internal fun getWeeks(yearMonth: YearMonth, firstDayOfWeek: DayOfWeek): List<Week> {
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
                Day(date = date, inCurrentMonth = date.yearMonth == yearMonth)
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