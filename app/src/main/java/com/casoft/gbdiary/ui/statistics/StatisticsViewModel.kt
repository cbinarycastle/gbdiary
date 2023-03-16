package com.casoft.gbdiary.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.GetStatisticsUseCase
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.Statistics
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.model.Message
import com.casoft.gbdiary.util.WhileViewSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getStatisticsUseCase: GetStatisticsUseCase,
) : ViewModel() {

    private val _yearMonth = MutableStateFlow<YearMonth>(YearMonth.now())
    val yearMonth = _yearMonth.asStateFlow()

    val statistics: StateFlow<Statistics> = yearMonth
        .map { yearMonth -> getStatisticsUseCase(yearMonth) }
        .onEach { result ->
            if (result is Result.Error) {
                _message.emit(Message.Toast("통계 데이터를 불러오지 못했습니다."))
            }
        }
        .map { it.data ?: Statistics(yearMonth.value, emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = WhileViewSubscribed,
            initialValue = Statistics(yearMonth.value, emptyList())
        )

    val hasNextMonth = yearMonth
        .map { it < YearMonth.now() }
        .stateIn(
            scope = viewModelScope,
            started = WhileViewSubscribed,
            initialValue = false
        )

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    fun moveToYearMonth(yearMonth: YearMonth) {
        _yearMonth.value = yearMonth
    }

    fun moveToPreviousMonth() {
        _yearMonth.value = yearMonth.value.minusMonths(1)
    }

    fun moveToNextMonth() {
        _yearMonth.value = yearMonth.value.plusMonths(1)
    }
}