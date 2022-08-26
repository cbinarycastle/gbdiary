package com.casoft.gbdiary.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.GetLocalImagesUseCase
import com.casoft.gbdiary.model.MAX_NUMBER_OF_IMAGES
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    getLocalImagesUseCase: GetLocalImagesUseCase,
) : ViewModel() {

    var maxSelectionCount = 0

    val images = flow {
        val result = getLocalImagesUseCase(Unit)
        if (result is Result.Success) {
            val uiStates = result.data
                .map { ImageUiState(it) }
                .chunked(NUMBER_OF_IMAGES_BY_ROW)
            emit(uiStates)
        } else {
            _message.emit(Message.ToastMessage("사진을 불러오지 못했습니다."))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = listOf()
    )

    private val _numberOfSelectedImages = MutableStateFlow(0)
    val numberOfSelectedImages = _numberOfSelectedImages.asStateFlow()

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    fun selectImage(uiState: ImageUiState) {
        if (numberOfSelectedImages.value >= maxSelectionCount && uiState.selected.not()) {
            viewModelScope.launch {
                _message.emit(
                    Message.AlertDialogMessage(
                        text = "사진은 최대 ${MAX_NUMBER_OF_IMAGES}장까지 첨부 가능해요!",
                        confirmText = "확인"
                    )
                )
            }
        } else {
            uiState.toggleSelect()
            if (uiState.selected) {
                _numberOfSelectedImages.value++
            } else {
                _numberOfSelectedImages.value--
            }
        }
    }
}