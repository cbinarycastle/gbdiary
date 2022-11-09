package com.casoft.gbdiary.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.GetDiaryItemsUseCase
import com.casoft.gbdiary.domain.IsPremiumUserUseCase
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimelineViewModel @Inject constructor(
    isPremiumUserUseCase: IsPremiumUserUseCase,
    getDiaryItemsUseCase: GetDiaryItemsUseCase,
) : ViewModel() {

    private val _yearMonth = MutableStateFlow(YearMonth.now())
    val yearMonth = _yearMonth.asStateFlow()

    val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val diaryItems = yearMonth
        .flatMapLatest { getDiaryItemsUseCase(it) }
        .map { result ->
            when (result) {
                is Result.Success -> {
                    result.data.sortedByDescending { it.date }
                }
                is Result.Error -> {
                    _message.emit(Message.Toast("타임라인을 불러오지 못했습니다."))
                    listOf()
                }
                is Result.Loading -> listOf()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    fun moveToYearMonth(yearMonth: YearMonth) {
        _yearMonth.value = yearMonth
    }

    fun moveToBeforeMonth() {
        _yearMonth.value = yearMonth.value.minusMonths(1)
    }

    fun moveToNextMonth() {
        _yearMonth.value = yearMonth.value.plusMonths(1)
    }
}