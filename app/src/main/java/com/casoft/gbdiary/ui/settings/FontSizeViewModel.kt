package com.casoft.gbdiary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.ObserveDiaryFontSizeUseCase
import com.casoft.gbdiary.domain.SetDiaryFontSizeUseCase
import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FontSizeViewModel @Inject constructor(
    observeDiaryFontSizeUseCase: ObserveDiaryFontSizeUseCase,
    private val setDiaryFontSizeUseCase: SetDiaryFontSizeUseCase,
) : ViewModel() {

    val diaryFontSize = observeDiaryFontSizeUseCase(Unit)
        .map { it.data ?: DiaryFontSize.M }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DiaryFontSize.M
        )

    fun setDiaryFontSize(diaryFontSize: DiaryFontSize) {
        viewModelScope.launch {
            setDiaryFontSizeUseCase(diaryFontSize)
        }
    }
}