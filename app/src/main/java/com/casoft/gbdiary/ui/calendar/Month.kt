package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.model.Sticker
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

data class Month(
    val yearMonth: YearMonth,
    val dayOfWeeks: List<DayOfWeek>,
    val weeks: List<Week>,
)

@Composable
fun Month(
    month: Month,
    today: LocalDate,
    stickers: Map<LocalDate, Sticker?>,
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
                today = today,
                stickers = stickers,
                onDayClick = onDayClick
            )
        }
    }
}