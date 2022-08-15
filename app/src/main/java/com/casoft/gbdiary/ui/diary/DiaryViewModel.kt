package com.casoft.gbdiary.ui.diary

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.casoft.gbdiary.model.Sticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

const val MAX_STICKERS = 4

class DiaryViewModel() : ViewModel() {

    private val _stickers = MutableStateFlow<List<Sticker>>(emptyList())
    val stickers = _stickers.asStateFlow()

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _images = MutableStateFlow<List<Uri>>(listOf())
    val images = _images.asStateFlow()

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
        _text.value = text
    }

    fun addImages(images: List<Uri>) {
        _images.value = _images.value + images
    }

    fun removeImage(index: Int) {
        _images.value = _images.value.filterIndexed { i, _ -> i != index }
    }
}