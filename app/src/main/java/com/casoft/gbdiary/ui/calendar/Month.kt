package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class Month(
    val yearMonth: YearMonth,
    val dayOfWeeks: List<DayOfWeek>,
    val weeks: List<Week>,
)

@Composable
fun Month(
    month: Month,
    dayStateList: DayStateList,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        month.weeks.forEach { week ->
            Week(
                week = week,
                dayStateList = dayStateList,
                today = today,
                onDayClick = onDayClick
            )
        }
    }
}