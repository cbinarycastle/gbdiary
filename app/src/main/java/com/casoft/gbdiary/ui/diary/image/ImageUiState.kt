package com.casoft.gbdiary.ui.diary.image

import androidx.compose.runtime.Immutable
import com.casoft.gbdiary.model.LocalImage

const val NUMBER_OF_IMAGES_BY_ROW = 3

@Immutable
data class ImageUiState(
    val image: LocalImage,
    val selected: Boolean,
) {
    fun toggleSelected(): ImageUiState = ImageUiState(image, !selected)
}