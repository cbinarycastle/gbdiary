package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class Week(val days: List<Day>)

@Composable
fun Week(
    week: Week,
    modifier: Modifier = Modifier,
    dayContent: @Composable (Day) -> Unit = { Day(it) },
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