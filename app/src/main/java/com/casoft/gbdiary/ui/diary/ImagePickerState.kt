package com.casoft.gbdiary.ui.diary

import android.net.Uri
import androidx.compose.runtime.*
import com.casoft.gbdiary.ui.extension.contentUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

const val NUMBER_OF_IMAGES_BY_ROW = 3

@Composable
fun rememberImagePickerState(
    viewModel: ImagePickerViewModel,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember(viewModel) {
    ImagePickerState(viewModel, coroutineScope)
}

@Stable
class ImagePickerState(
    viewModel: ImagePickerViewModel,
    coroutineScope: CoroutineScope,
) {
    val images = viewModel.images
        .map { images ->
            images
                .map { image -> Image(image.contentUri()) }
                .chunked(NUMBER_OF_IMAGES_BY_ROW)
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )
}

@Stable
class Image(
    val uri: Uri,
    selected: Boolean = false,
) {
    var selected by mutableStateOf(selected)

    fun toggleSelect() {
        selected = !selected
    }
}