package com.casoft.gbdiary.ui.diary.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.GetLocalImagesUseCase
import com.casoft.gbdiary.domain.IsPremiumUserUseCase
import com.casoft.gbdiary.image.ImageFileStorage
import com.casoft.gbdiary.model.*
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    getLocalImagesUseCase: GetLocalImagesUseCase,
    isPremiumUserUseCase: IsPremiumUserUseCase,
    private val imageFileStorage: ImageFileStorage,
) : ViewModel() {

    private var preSelectionCount = 0

    private val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    private val imageUiStates = MutableStateFlow<List<ImageUiState>>(emptyList())

    val images: StateFlow<List<List<ImageUiState>>> = imageUiStates
        .map { it.chunked(NUMBER_OF_IMAGES_BY_ROW) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    private val selectedImages: StateFlow<List<LocalImage>> = imageUiStates.map { imageUiStates ->
        imageUiStates.filter { it.selected }.map { it.image }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    val numberOfSelectedImages: StateFlow<Int> = selectedImages
        .map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    private val _selectionFinished = MutableSharedFlow<List<File>>()
    val selectionFinished = _selectionFinished.asSharedFlow()

    init {
        viewModelScope.launch {
            val result = getLocalImagesUseCase(Unit)
            if (result is Result.Success) {
                imageUiStates.value = result.data.map { ImageUiState(image = it, selected = false) }
            } else {
                _message.emit(Message.Toast("사진을 불러오지 못했습니다."))
            }
        }
    }

    fun setPreSelectionCount(preSelectionCount: Int) {
        this.preSelectionCount = preSelectionCount
    }

    fun toggleSelected(imageUiState: ImageUiState) {
        if (imageUiState.selected.not()) {
            val isPremiumUser = isPremiumUser.value
            val numberOfSelectedImages = preSelectionCount + numberOfSelectedImages.value

            if (isPremiumUser.not() && numberOfSelectedImages >= MAX_IMAGES_FOR_STANDARD_USER) {
                val errorMessage = Message.AlertDialog(
                    text = "사진은 최대 ${MAX_IMAGES_FOR_STANDARD_USER}장까지 첨부 가능해요!\n" +
                        "${MAX_IMAGES_FOR_PREMIUM_USER}장까지 추가하려면 이용권 구매가 필요합니다",
                    confirmText = "확인"
                )
                viewModelScope.launch {
                    _message.emit(errorMessage)
                }
                return
            } else if (isPremiumUser && numberOfSelectedImages >= MAX_IMAGES_FOR_PREMIUM_USER) {
                val errorMessage = Message.AlertDialog(
                    text = "사진은 최대 ${MAX_IMAGES_FOR_PREMIUM_USER}장까지 첨부 가능해요!",
                    confirmText = "확인"
                )
                viewModelScope.launch {
                    _message.emit(errorMessage)
                }
                return
            }
        }

        imageUiStates.value = imageUiStates.value.map {
            if (it.image == imageUiState.image) {
                it.toggleSelected()
            } else {
                it
            }
        }
    }

    fun finishSelection() {
        viewModelScope.launch {
            val savedImages = imageFileStorage.save(selectedImages.value)
            _selectionFinished.emit(savedImages)
        }
    }
}