package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.threeten.bp.DayOfWeek
import org.threeten.bp.format.TextStyle
import java.util.*

@Composable
fun WeekHeader(
    modifier: Modifier = Modifier,
    dayOfWeeks: Array<DayOfWeek> = arrayOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
    ),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(HorizontalSpaceBetweenCells)
    ) {
        dayOfWeeks.forEach { dayOfWeek ->
            Box(
                modifier = Modifier.size(CellSize),
                contentAlignment = Alignment.Center
            ) {
                Text(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
            }
        }
    }
}