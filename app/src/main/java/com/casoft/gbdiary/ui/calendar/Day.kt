package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.threeten.bp.LocalDate

data class Day(val date: LocalDate, val inCurrentMonth: Boolean)

@Composable
fun RowScope.Day(
    day: Day,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .weight(1f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if (day.inCurrentMonth) {
            Text(day.date.dayOfMonth.toString())
        }
    }
}