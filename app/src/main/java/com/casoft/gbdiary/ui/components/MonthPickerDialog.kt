package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.calendar.PAGE_COUNT
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import org.threeten.bp.YearMonth

private val MonthsByRow = (1..12).chunked(4)
private val PagesFromToday = (PAGE_COUNT / 2 - 1).toLong()

@Composable
fun MonthPickerDialog(
    initialYear: Int,
    today: YearMonth,
    onSelect: (YearMonth) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentYear by remember { mutableStateOf(initialYear) }
    val minYearMonth by remember(today) { mutableStateOf(today.minusMonths(PagesFromToday)) }
    val maxYearMonth by remember(today) { mutableStateOf(today.plusMonths(PagesFromToday)) }

    Dialog(onDismissRequest = onDismiss) {
        CompositionLocalProvider(LocalTextStyle provides GBDiaryTheme.typography.subtitle1) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 12.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    )
                ) {
                    Header(
                        year = currentYear,
                        onBeforeClick = {
                            if (currentYear > minYearMonth.year) {
                                currentYear--
                            }
                        },
                        onNextClick = {
                            if (currentYear < maxYearMonth.year) {
                                currentYear++
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                    Months(
                        year = currentYear,
                        today = today,
                        minYearMonth = minYearMonth,
                        maxYearMonth = maxYearMonth,
                        onMonthClick = { month -> onSelect(YearMonth.of(currentYear, month)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    year: Int,
    onBeforeClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = onBeforeClick) {
            Icon(
                painter = painterResource(R.drawable.chevron_left),
                contentDescription = "이전"
            )
        }
        Text("${year}년")
        IconButton(onClick = onNextClick) {
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = "다음"
            )
        }
    }
}

@Composable
private fun Months(
    year: Int,
    today: YearMonth,
    minYearMonth: YearMonth,
    maxYearMonth: YearMonth,
    onMonthClick: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MonthsByRow.forEach { months ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                months.forEach { month ->
                    val yearMonth = YearMonth.of(year, month)
                    DateBox(
                        text = month.toString(),
                        onClick = { onMonthClick(month) },
                        isToday = yearMonth == today,
                        modifier = Modifier
                            .size(44.dp)
                            .then(
                                if (yearMonth < minYearMonth || yearMonth > maxYearMonth) {
                                    Modifier.alpha(0.2f)
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
        }
    }
}