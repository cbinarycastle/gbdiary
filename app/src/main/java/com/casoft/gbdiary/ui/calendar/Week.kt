package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.casoft.gbdiary.model.Sticker
import org.threeten.bp.LocalDate

data class Week(val days: List<Day>)

@Composable
fun Week(
    week: Week,
    today: LocalDate,
    stickers: Map<LocalDate, Sticker>,
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
                sticker = stickers[day.date],
                onClick = onDayClick
            )
        }
    }
}