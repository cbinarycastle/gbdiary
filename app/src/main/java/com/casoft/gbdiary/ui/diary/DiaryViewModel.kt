package com.casoft.gbdiary.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.di.ApplicationScope
import com.casoft.gbdiary.domain.*
import com.casoft.gbdiary.model.*
import com.casoft.gbdiary.ui.extension.toFile
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

const val MAX_STICKERS = 4

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiaryViewModel @Inject constructor(
    isPremiumUserUseCase: IsPremiumUserUseCase,
    private val getDiaryItemUseCase: GetDiaryItemUseCase,
    private val saveDiaryItemUseCase: SaveDiaryItemUseCase,
    private val deleteDiaryItemUseCase: DeleteDiaryItemUseCase,
    observeDiaryFontSizeUseCase: ObserveDiaryFontSizeUseCase,
    observeTextAlignUseCase: ObserveTextAlignUseCase,
    private val setTextAlignUseCase: SetTextAlignUseCase,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val _date = MutableStateFlow<LocalDate>(LocalDate.now())
    val date = _date.asStateFlow()

    val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    private val _existsDiary = MutableStateFlow<Boolean?>(null)
    val existsDiary = _existsDiary.asStateFlow()

    private val stickerList = mutableListOf<Sticker>()

    private val _stickers = MutableStateFlow<List<Sticker>>(listOf())
    val stickers = _stickers.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _images = MutableStateFlow<List<File>>(listOf())
    val images = _images.asStateFlow()

    private val existingDiary = _date.filterNotNull()
        .flatMapLatest { getDiaryItemUseCase(it) }
        .map { result ->
            when (result) {
                is Result.Success -> result.data
                is Result.Error -> {
                    _message.emit(
                        Message.Toast("작성된 일기를 불러오지 못했습니다.")
                    )
                    null
                }
                is Result.Loading -> null
            }
        }
        .onEach { diaryItem -> _existsDiary.value = diaryItem != null }
        .filterNotNull()
        .onEach { diaryItem ->
            stickerList.run {
                clear()
                addAll(diaryItem.stickers)
            }
            publishStickerList()
            _content.value = diaryItem.content
            _images.value = diaryItem.images.map { filePath -> File(filePath) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _initialStickerBottomSheetShown = MutableStateFlow(false)
    val initialStickerBottomSheetShown = _initialStickerBottomSheetShown.asStateFlow()

    private val isValidToSave = combine(
        existingDiary,
        stickers,
        content,
        images
    ) { existingDiary, stickers, content, images ->
        val isNotEmpty = stickers.isNotEmpty() || content.isNotBlank() || images.isNotEmpty()
        val isChanged = existingDiary?.let { existing ->
            existing.stickers != stickers ||
                existing.content != content ||
                existing.images != images.map { image -> image.path }
        } ?: true
        isNotEmpty && isChanged
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    val contentFontSize = observeDiaryFontSizeUseCase(Unit)
        .map { it.data ?: DiaryFontSize.Default }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DiaryFontSize.Default
        )

    val textAlign = observeTextAlignUseCase(Unit)
        .map { it.successOr(TextAlign.LEFT) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TextAlign.LEFT
        )

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    private val _navigateToImagePicker = MutableSharedFlow<Int>()
    val navigateToImagePicker = _navigateToImagePicker.asSharedFlow()

    fun setDate(date: LocalDate) {
        _date.value = date
    }

    fun addSticker(sticker: Sticker) {
        if (stickerList.size < MAX_STICKERS) {
            stickerList.add(sticker)
        }
        publishStickerList()
    }

    fun changeSticker(index: Int, sticker: Sticker) {
        stickerList[index] = sticker
        publishStickerList()
    }

    fun removeSticker(index: Int) {
        stickerList.removeAt(index)
        publishStickerList()
    }

    private fun publishStickerList() {
        _stickers.value = stickerList.toList()
    }

    fun inputText(text: String) {
        _content.value = text
    }

    fun addImages(images: List<LocalImage>) {
        _images.value = _images.value + images.map { it.toFile() }
    }

    fun removeImage(index: Int) {
        _images.value = _images.value.filterIndexed { i, _ -> i != index }
    }

    fun toggleTextAlign() {
        viewModelScope.launch {
            setTextAlignUseCase(textAlign.value.toggleSelect())
        }
    }

    fun onAlbumClick() {
        val isPremiumUser = isPremiumUser.value
        val images = images.value

        val errorMessage = when {
            isPremiumUser.not() && images.size >= MAX_IMAGES_FOR_STANDARD_USER -> {
                Message.AlertDialog(
                    text = "사진은 최대 ${MAX_IMAGES_FOR_STANDARD_USER}장까지 첨부 가능해요!\n" +
                        "${MAX_IMAGES_FOR_PREMIUM_USER}장까지 추가하려면 이용권 구매가 필요합니다",
                    confirmText = "확인"
                )
            }
            isPremiumUser && images.size >= MAX_IMAGES_FOR_PREMIUM_USER -> {
                Message.AlertDialog(
                    text = "사진은 최대 ${MAX_IMAGES_FOR_PREMIUM_USER}장까지 첨부 가능해요!",
                    confirmText = "확인"
                )
            }
            else -> null
        }

        viewModelScope.launch {
            if (errorMessage == null) {
                _navigateToImagePicker.emit(images.size)
            } else {
                _message.emit(errorMessage)
            }
        }
    }

    fun saveDiary(shouldShowMessage: Boolean = true) {
        if (isValidToSave.value.not()) {
            return
        }

        applicationScope.launch {
            val result = saveDiaryItemUseCase(
                SaveDiaryItemUseCase.Params(
                    date = date.value,
                    stickers = stickers.value,
                    content = content.value,
                    images = images.value
                )
            )

            if (shouldShowMessage) {
                when (result) {
                    is Result.Success -> {
                        _message.emit(
                            Message.Toast("저장이 완료되었습니다.")
                        )
                    }
                    is Result.Error -> {
                        _message.emit(
                            Message.Toast("저장 도중 오류가 발생했습니다.")
                        )
                    }
                    is Result.Loading -> {}
                }
            }
        }
    }

    fun deleteDiary() {
        existingDiary.value?.let {
            viewModelScope.launch {
                deleteDiaryItemUseCase(it)
            }
        }
    }

    fun onShowStickerBottomSheetInitially() {
        _initialStickerBottomSheetShown.value = true
    }
}

private fun TextAlign.toggleSelect(): TextAlign {
    return if (this == TextAlign.LEFT) {
        TextAlign.CENTER
    } else {
        TextAlign.LEFT
    }
}