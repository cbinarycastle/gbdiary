package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.time.LocalDate

data class Week(val days: List<Day>)

@Composable
fun Week(
    week: Week,
    dayStateList: DayStateList,
    today: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceBetweenCells)
    ) {
        week.days.forEach { day ->
            Day(
                day = day,
                today = today,
                state = dayStateList[day.date],
                onClick = onDayClick
            )
        }
    }
}