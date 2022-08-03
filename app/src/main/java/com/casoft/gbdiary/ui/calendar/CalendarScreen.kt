package com.casoft.gbdiary.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import org.threeten.bp.format.TextStyle
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CalendarScreen() {
    val state = rememberCalendarState(
        selectionState = rememberSelectionState(SelectionMode.SINGLE)
    )

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val yearMonth = state.currentYearMonth
            Text(
                text = yearMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale.getDefault()
                ),
                fontSize = 20.sp
            )
            Spacer(Modifier.height(32.dp))
            Calendar(
                modifier = Modifier.height(314.dp),
                state = state,
            ) { month ->
                Column(Modifier.fillMaxHeight()) {
                    Month(
                        month = month,
                        selectionState = state.selectionState,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}