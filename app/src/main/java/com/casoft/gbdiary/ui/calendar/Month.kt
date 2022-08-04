package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.threeten.bp.DayOfWeek
import org.threeten.bp.YearMonth

data class Month(
    val yearMonth: YearMonth,
    val dayOfWeeks: List<DayOfWeek>,
    val weeks: List<Week>,
)

@Composable
fun Month(
    month: Month,
    selectionState: SelectionState,
    modifier: Modifier,
    weekContent: @Composable (Week) -> Unit = { Week(week = it, selectionState = selectionState) },
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        month.weeks.forEach {
            weekContent(it)
        }
    }
}