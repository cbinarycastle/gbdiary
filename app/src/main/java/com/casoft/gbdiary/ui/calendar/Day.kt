package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.imageResId
import com.casoft.gbdiary.ui.modifier.alignTopToCenterOfParent
import com.casoft.gbdiary.ui.theme.GBDiaryContentAlpha
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.ui.theme.markerPainter
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
                if (day.date == today) {
                    TodayMarker(Modifier.alignTopToCenterOfParent())
                }
                Text(
                    text = day.date.dayOfMonth.toString(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .then(
                            if (day.date > today) {
                                Modifier.alpha(GBDiaryContentAlpha.disabled)
                            } else {
                                Modifier
                            }
                        ),
                    style = GBDiaryTheme.typography.caption
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
                Sticker(state.sticker)
            }

            if (day.date <= today) {
                ClickableArea(
                    onClick = { onClick(day.date) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun TodayMarker(modifier: Modifier = Modifier) {
    Image(
        painter = markerPainter(),
        contentDescription = "오늘",
        modifier = modifier
    )
}

@Composable
private fun Sticker(sticker: Sticker) {
    Image(
        painter = painterResource(sticker.imageResId),
        contentDescription = sticker.name
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ClickableArea(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = Color.Transparent,
        content = {}
    )
}