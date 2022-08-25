package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.ui.modifier.noRippleClickable
import com.casoft.gbdiary.ui.theme.GBDiaryContentAlpha
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime

private val AmPms = AmPm.values()
private val Hours = (1..12).toList()
private val Minutes = (0..59).toList()

@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    var amPmHour by remember(initialTime) { mutableStateOf(initialTime.amPmHour) }
    var minute by remember(initialTime) { mutableStateOf(initialTime.minute) }
    val time = remember(amPmHour, minute) { LocalTime.of(amPmHour.hourOfDay, minute) }

    GBDiaryAlertDialog(
        onConfirm = { onConfirm(time) },
        onDismiss = onDismiss,
        content = {
            Surface(Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    AmPmPager(
                        initialAmPm = amPmHour.amPm,
                        onSelectedValueChange = { amPmHour = amPmHour.copy(amPm = it) },
                        modifier = Modifier.weight(1f)
                    )
                    HourPager(
                        initialHour = amPmHour.hour,
                        onSelectedValueChange = { amPmHour = amPmHour.copy(hour = it) },
                        modifier = Modifier.weight(1f)
                    )
                    Text(":")
                    MinutePager(
                        initialMinute = minute,
                        onSelectedValueChange = { minute = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmText = { Text("확인") },
        dismissText = { Text("취소") }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun AmPmPager(
    initialAmPm: AmPm,
    onSelectedValueChange: (AmPm) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = AmPms.indexOf(initialAmPm))

    TimePager(
        count = AmPms.size,
        text = { page -> AmPms[page].text },
        onSelectedPageChange = { onSelectedValueChange(AmPms[it]) },
        pagerState = pagerState,
        modifier = modifier
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun HourPager(
    initialHour: Int,
    onSelectedValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = Hours.indexOf(initialHour))

    TimePager(
        count = Hours.size,
        text = { page -> Hours[page].toString() },
        onSelectedPageChange = { onSelectedValueChange(Hours[it]) },
        pagerState = pagerState,
        modifier = modifier
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MinutePager(
    initialMinute: Int,
    onSelectedValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = Minutes.indexOf(initialMinute))

    TimePager(
        count = Minutes.size,
        text = { page -> Minutes[page].toString().padStart(2, '0') },
        onSelectedPageChange = { onSelectedValueChange(Minutes[it]) },
        pagerState = pagerState,
        modifier = modifier
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TimePager(
    count: Int,
    text: (page: Int) -> String,
    onSelectedPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    LaunchedEffect(pagerState, coroutineScope) {
        snapshotFlow { pagerState.currentPage }
            .collect { page -> onSelectedPageChange(page) }
    }

    VerticalPager(
        count = count,
        state = pagerState,
        contentPadding = PaddingValues(vertical = 72.dp),
        modifier = modifier
    ) { page ->
        val selected = page == pagerState.currentPage

        Text(
            text = text(page),
            modifier = Modifier
                .noRippleClickable {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                }
                .then(
                    if (selected) {
                        Modifier
                    } else {
                        Modifier.alpha(GBDiaryContentAlpha.disabled)
                    }
                )
        )
    }
}