package com.casoft.gbdiary.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.model.Sticker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MAX_STICKERS = 4

class DiaryViewModel : ViewModel() {

    private val _stickers = MutableStateFlow<List<Sticker>>(emptyList())
    val stickers = _stickers.asStateFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    fun addSticker(sticker: Sticker) {
        val selectedStickers = _stickers.value
        if (selectedStickers.size == MAX_STICKERS) {
            viewModelScope.launch {
                _message.emit("스티커는 4개까지 추가할 수 있어요.")
            }
            return
        }

        _stickers.value = selectedStickers + sticker
    }

    fun removeSticker(index: Int) {
        _stickers.value = _stickers.value.filterIndexed { i, _ -> i != index }
    }

    fun inputText(text: String) {
        _text.value = text
    }
}