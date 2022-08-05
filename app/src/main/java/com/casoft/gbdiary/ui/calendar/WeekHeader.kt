package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.casoft.gbdiary.ui.theme.GBDiaryContentAlpha
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
        CompositionLocalProvider(LocalContentAlpha provides GBDiaryContentAlpha.disabled) {
            dayOfWeeks.forEach { dayOfWeek ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                }
            }
        }
    }
}