package com.casoft.gbdiary.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.GetDiaryItemsUseCase
import com.casoft.gbdiary.domain.IsPremiumUserUseCase
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.model.successOr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    isPremiumUserUseCase: IsPremiumUserUseCase,
    private val getDiaryItemsUseCase: GetDiaryItemsUseCase,
) : ViewModel() {

    var currentYearMonth: YearMonth = YearMonth.now()

    val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun getDayStateList(yearMonth: YearMonth): Flow<DayStateList> {
        return getDiaryItemsUseCase(yearMonth)
            .map { result ->
                val dayStates = result.successOr(listOf())
                    .associate { item -> item.date to DayState(item.stickers.firstOrNull()) }
                DayStateList(dayStates)
            }
    }
}