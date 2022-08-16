package com.casoft.gbdiary.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.di.ApplicationScope
import com.casoft.gbdiary.domain.GetDiaryItemUseCase
import com.casoft.gbdiary.domain.SaveDiaryItemUseCase
import com.casoft.gbdiary.model.LocalImage
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.extension.toFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import java.io.File
import javax.inject.Inject

const val MAX_STICKERS = 4

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val getDiaryItemUseCase: GetDiaryItemUseCase,
    private val saveDiaryItemUseCase: SaveDiaryItemUseCase,
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val _date = MutableStateFlow<LocalDate>(LocalDate.now())
    val date = _date.asStateFlow()

    val existingDiary = _date.filterNotNull()
        .flatMapLatest { getDiaryItemUseCase(it) }
        .map { it.data ?: _message.emit("작성된 일기를 불러오지 못했습니다.").let { null } }
        .filterNotNull()
        .onEach {
            _stickers.value = it.stickers
            _content.value = it.content
            _images.value = it.images.map { filePath -> File(filePath) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _stickers = MutableStateFlow<List<Sticker>>(emptyList())
    val stickers = _stickers.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _images = MutableStateFlow<List<File>>(listOf())
    val images = _images.asStateFlow()

    val isValidToSave = combine(
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

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun setDate(date: LocalDate) {
        _date.value = date
    }

    fun addSticker(sticker: Sticker) {
        val selectedStickers = _stickers.value
        if (selectedStickers.size < MAX_STICKERS) {
            _stickers.value = selectedStickers + sticker
        }
    }

    fun removeSticker(index: Int) {
        _stickers.value = _stickers.value.filterIndexed { i, _ -> i != index }
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
                    is Result.Success -> _message.emit("저장이 완료되었습니다.")
                    is Result.Error -> _message.emit("저장 도중 오류가 발생했습니다.")
                    Result.Loading -> {}
                }
            }
        }
    }
}