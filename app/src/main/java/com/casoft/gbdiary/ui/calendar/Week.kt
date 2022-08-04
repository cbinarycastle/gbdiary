package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Week(val days: List<Day>)

@Composable
fun Week(
    week: Week,
    selectionState: SelectionState,
    modifier: Modifier = Modifier,
    dayContent: @Composable (Day) -> Unit = { Day(day = it, selectionState = selectionState) },
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        week.days.forEach {
            dayContent(it)
        }
    }
}