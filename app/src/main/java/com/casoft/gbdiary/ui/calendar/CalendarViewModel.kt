package com.casoft.gbdiary.ui.calendar

import androidx.lifecycle.ViewModel
import com.casoft.gbdiary.domain.GetDiaryItemsUseCase
import com.casoft.gbdiary.model.successOr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getDiaryItemsUseCase: GetDiaryItemsUseCase,
) : ViewModel() {

    var currentYearMonth: YearMonth = YearMonth.now()

    fun getDayStateList(yearMonth: YearMonth): Flow<DayStateList> {
        return getDiaryItemsUseCase(yearMonth)
            .map { result ->
                val dayStates = result.successOr(listOf())
                    .associate { item -> item.date to DayState(item.stickers.firstOrNull()) }
                DayStateList(dayStates)
            }
    }
}