package com.casoft.gbdiary.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.GetLocalImagesUseCase
import com.casoft.gbdiary.domain.IsPremiumUserUseCase
import com.casoft.gbdiary.model.MAX_IMAGES_FOR_PREMIUM_USER
import com.casoft.gbdiary.model.MAX_IMAGES_FOR_STANDARD_USER
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    getLocalImagesUseCase: GetLocalImagesUseCase,
    isPremiumUserUseCase: IsPremiumUserUseCase,
) : ViewModel() {

    var preSelectionCount = 0

    private val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

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

    fun toggleImage(uiState: ImageUiState) {
        if (uiState.selected.not()) {
            val totalSelectionCount = numberOfSelectedImages.value + preSelectionCount
            val isPremiumUser = isPremiumUser.value

            val errorMessage = when {
                isPremiumUser.not() && totalSelectionCount >= MAX_IMAGES_FOR_STANDARD_USER -> {
                    Message.AlertDialogMessage(
                        text = "사진은 최대 ${MAX_IMAGES_FOR_STANDARD_USER}장까지 첨부 가능해요!\n" +
                            "${MAX_IMAGES_FOR_PREMIUM_USER}장까지 추가하려면 이용권 구매가 필요합니다",
                        confirmText = "확인"
                    )
                }
                isPremiumUser && totalSelectionCount >= MAX_IMAGES_FOR_PREMIUM_USER -> {
                    Message.AlertDialogMessage(
                        text = "사진은 최대 ${MAX_IMAGES_FOR_PREMIUM_USER}장까지 첨부 가능해요!",
                        confirmText = "확인"
                    )
                }
                else -> null
            }

            if (errorMessage != null) {
                viewModelScope.launch {
                    _message.emit(errorMessage)
                }
                return
            }
        }

        uiState.toggleSelect()
        if (uiState.selected) {
            _numberOfSelectedImages.value++
        } else {
            _numberOfSelectedImages.value--
        }
    }
}