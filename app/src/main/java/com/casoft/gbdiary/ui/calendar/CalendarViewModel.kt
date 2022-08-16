package com.casoft.gbdiary.ui.calendar

import androidx.lifecycle.ViewModel
import com.casoft.gbdiary.domain.GetDiaryItemsUseCase
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.successOr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getDiaryItemsUseCase: GetDiaryItemsUseCase,
) : ViewModel() {

    fun getStickers(yearMonth: YearMonth): Flow<Map<LocalDate, Sticker?>> {
        return getDiaryItemsUseCase(yearMonth)
            .map { result ->
                result.successOr(listOf())
                    .associate { item -> item.date to item.stickers.firstOrNull() }
            }
    }
}