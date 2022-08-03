package com.casoft.gbdiary.ui.calendar

import androidx.compose.runtime.*

@Stable
class SelectionState(private val selectionMode: SelectionMode) {

    var selectedDateRange by mutableStateOf<DateRange?>(null)
        private set

    fun select(day: Day) {
        when (selectionMode) {
            SelectionMode.SINGLE -> {
                selectedDateRange = day.date..day.date
            }
            SelectionMode.PERIOD -> {
                val startDate = selectedDateRange?.start
                selectedDateRange = if (startDate == null ||
                    day.date <= startDate ||
                    isFullySelected()
                ) {
                    day.date..day.date
                } else {
                    startDate..day.date
                }
            }
            SelectionMode.NONE -> {}
        }
    }

    fun isFullySelected(): Boolean {
        return selectedDateRange?.let { listOf(it).flatten().size > 1 }
            ?: false
    }

    fun isSelected(day: Day): Boolean = selectedDateRange?.contains(day.date) ?: false

    fun isStart(day: Day): Boolean = day.date == selectedDateRange?.start

    fun isEnd(day: Day): Boolean = day.date == selectedDateRange?.endInclusive
}

@Composable
fun rememberSelectionState(selectionMode: SelectionMode): SelectionState = remember {
    SelectionState(selectionMode)
}