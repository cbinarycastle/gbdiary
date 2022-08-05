package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import org.threeten.bp.LocalDate

data class Day(val date: LocalDate, val inCurrentMonth: Boolean)

fun Day.isToday() = this.date == LocalDate.now()

@Composable
fun RowScope.Day(
    day: Day,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .weight(1f)
            .aspectRatio(1f)
    ) {
        if (day.inCurrentMonth) {
            if (day.isToday()) {
                TodayMarker(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                )
            }
            Text(
                text = day.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun TodayMarker(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(
            if (GBDiaryTheme.colors.isLight) {
                R.drawable.marker_light
            } else {
                R.drawable.marker_dark
            }
        ),
        contentDescription = "오늘",
        modifier = modifier
    )
}