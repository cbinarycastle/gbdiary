package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.threeten.bp.LocalDate

data class Week(val days: List<Day>)

@Composable
fun Week(
    week: Week,
    today: LocalDate,
    modifier: Modifier = Modifier,
    dayContent: @Composable RowScope.(Day) -> Unit = { Day(day = it, today = today) },
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceBetweenCells)
    ) {
        week.days.forEach {
            dayContent(it)
        }
    }
}