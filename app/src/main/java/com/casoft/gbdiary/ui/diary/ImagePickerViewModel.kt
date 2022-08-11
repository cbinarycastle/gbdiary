package com.casoft.gbdiary.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.GetLocalImagesUseCase
import com.casoft.gbdiary.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    getLocalImagesUseCase: GetLocalImagesUseCase,
) : ViewModel() {

    val images = flow {
        val result = getLocalImagesUseCase(Unit)
        if (result is Result.Success) {
            emit(result.data)
        } else {
            _message.emit("사진을 불러오지 못했습니다.")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = listOf()
    )

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()
}