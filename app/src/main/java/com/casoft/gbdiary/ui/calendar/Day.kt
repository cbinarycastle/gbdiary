package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.components.DateBox
import com.casoft.gbdiary.ui.modifier.noRippleClickable
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import org.threeten.bp.LocalDate

data class Day(val date: LocalDate, val inCurrentMonth: Boolean)

@Composable
fun RowScope.Day(
    day: Day,
    today: LocalDate,
    state: DayState?,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .weight(1f)
            .aspectRatio(1f)
    ) {
        if (day.inCurrentMonth) {
            if (state?.sticker == null) {
                DateBox(
                    text = day.date.dayOfMonth.toString(),
                    onClick = { onClick(day.date) },
                    enabled = day.date <= today,
                    isToday = day.date == today,
                    modifier = Modifier.fillMaxSize()
                )
                if (state != null) {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp, end = 8.dp)
                            .size(4.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(GBDiaryTheme.colors.onBackground)
                    )
                }
            } else {
                Box(Modifier.noRippleClickable { onClick(day.date) }) {
                    Image(
                        painter = painterResource(state.sticker.imageResId),
                        contentDescription = state.sticker.name
                    )
                }
            }
        }
    }
}