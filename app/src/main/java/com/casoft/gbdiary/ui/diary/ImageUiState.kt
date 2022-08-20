package com.casoft.gbdiary.ui.diary

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.casoft.gbdiary.model.LocalImage

const val NUMBER_OF_IMAGES_BY_ROW = 3

@Stable
class ImageUiState(
    val localImage: LocalImage,
    selected: Boolean = false,
) {
    var selected by mutableStateOf(selected)

    fun toggleSelect() {
        selected = !selected
    }
}