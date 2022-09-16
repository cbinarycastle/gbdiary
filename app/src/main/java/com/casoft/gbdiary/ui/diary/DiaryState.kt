package com.casoft.gbdiary.ui.diary

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun rememberDiaryState(
    bottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    ),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
) = remember(bottomSheetState, coroutineScope, keyboardController) {
    DiaryState(bottomSheetState, coroutineScope, keyboardController)
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Stable
class DiaryState(
    val bottomSheetState: ModalBottomSheetState,
    private val coroutineScope: CoroutineScope,
    private val keyboardController: SoftwareKeyboardController?,
) {
    val isBottomSheetVisible: Boolean
        get() = bottomSheetState.isVisible

    var currentBottomSheet by mutableStateOf(DiaryBottomSheet.STICKER)
        private set

    var selectedStickerIndex by mutableStateOf<Int?>(null)
        private set

    var visibleRemoveStickerButtonIndex by mutableStateOf<Int?>(null)
        private set

    val isRemovingSticker: Boolean
        get() = visibleRemoveStickerButtonIndex != null

    var visibleRemoveImageButtonIndex by mutableStateOf<Int?>(null)
        private set

    var shouldShowPermissionDeniedDialog by mutableStateOf(false)
        private set

    var shouldShowConfirmDeleteDialog by mutableStateOf(false)
        private set

    val textFieldFocusRequester = FocusRequester()

    init {
        coroutineScope.launch {
            snapshotFlow { isBottomSheetVisible }
                .collect {
                    if (isBottomSheetVisible.not()) {
                        selectedStickerIndex = null
                    }
                }
        }
    }

    fun showBottomSheet(bottomSheet: DiaryBottomSheet) {
        keyboardController?.hide()

        currentBottomSheet = bottomSheet
        coroutineScope.launch {
            bottomSheetState.show()
        }
    }

    fun hideBottomSheet() {
        coroutineScope.launch {
            bottomSheetState.hide()
        }
    }

    fun selectSticker(index: Int) {
        selectedStickerIndex = index
        showBottomSheet(DiaryBottomSheet.STICKER)
    }

    fun startRemovingSticker(index: Int) {
        visibleRemoveStickerButtonIndex = index
    }

    fun cancelRemovingSticker() {
        visibleRemoveStickerButtonIndex = null
    }

    fun startRemovingImage(index: Int) {
        visibleRemoveImageButtonIndex = index
    }

    fun cancelRemovingImage() {
        visibleRemoveImageButtonIndex = null
    }

    fun showPermissionDeniedDialog() {
        shouldShowPermissionDeniedDialog = true
    }

    fun hidePermissionDeniedDialog() {
        shouldShowPermissionDeniedDialog = false
    }

    fun showConfirmDeleteDialog() {
        shouldShowConfirmDeleteDialog = true
    }

    fun hideConfirmDeleteDialog() {
        shouldShowConfirmDeleteDialog = false
    }

    fun requestFocusOnTextField() {
        textFieldFocusRequester.requestFocus()
    }
}

enum class DiaryBottomSheet {
    STICKER, MORE
}