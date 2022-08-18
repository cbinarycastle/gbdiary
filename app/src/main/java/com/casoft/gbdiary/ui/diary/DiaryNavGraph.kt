package com.casoft.gbdiary.ui.diary

import androidx.compose.material.ExperimentalMaterialApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.casoft.gbdiary.model.LocalImage
import org.threeten.bp.LocalDate

object DiaryDestinations {
    const val HOME_ROUTE = "diary/home"
    const val HOME_YEAR_KEY = "year"
    const val HOME_MONTH_KEY = "month"
    const val HOME_DAY_OF_MONTH_KEY = "dayOfMonth"
    const val IMAGE_PICKER_ROUTE = "diary/image/select"
    const val IMAGE_PICKER_MAX_SELECTION_COUNT_KEY = "maxSelectionCount"
}

@OptIn(ExperimentalMaterialApi::class)
fun NavGraphBuilder.diaryNavGraph(actions: DiaryActions) {
    composable(
        route = "${DiaryDestinations.HOME_ROUTE}/{${DiaryDestinations.HOME_YEAR_KEY}}/{${DiaryDestinations.HOME_MONTH_KEY}}/{${DiaryDestinations.HOME_DAY_OF_MONTH_KEY}}",
        arguments = listOf(
            navArgument(DiaryDestinations.HOME_YEAR_KEY) { type = NavType.IntType },
            navArgument(DiaryDestinations.HOME_MONTH_KEY) { type = NavType.IntType },
            navArgument(DiaryDestinations.HOME_DAY_OF_MONTH_KEY) { type = NavType.IntType },
        )
    ) {
        val arguments = requireNotNull(it.arguments)
        val year = arguments.getInt(DiaryDestinations.HOME_YEAR_KEY)
        val month = arguments.getInt(DiaryDestinations.HOME_MONTH_KEY)
        val dayOfMonth = arguments.getInt(DiaryDestinations.HOME_DAY_OF_MONTH_KEY)
        val diaryViewModel = hiltViewModel<DiaryViewModel>()
            .apply { setDate(LocalDate.of(year, month, dayOfMonth)) }
        DiaryScreen(
            viewModel = diaryViewModel,
            savedStateHandle = actions.savedStateHandle,
            onBack = { actions.navigateUp() },
            onAlbumClick = { maxSelectionCount ->
                actions.navigateToImagePicker(maxSelectionCount)
            }
        )
    }
    composable(
        route = "${DiaryDestinations.IMAGE_PICKER_ROUTE}/{${DiaryDestinations.IMAGE_PICKER_MAX_SELECTION_COUNT_KEY}}",
        arguments = listOf(
            navArgument(DiaryDestinations.IMAGE_PICKER_MAX_SELECTION_COUNT_KEY) {
                type = NavType.IntType
            }
        )
    ) {
        val arguments = requireNotNull(it.arguments)
        val maxSelectionCount = arguments.getInt(
            DiaryDestinations.IMAGE_PICKER_MAX_SELECTION_COUNT_KEY
        )
        val imagePickerViewModel = hiltViewModel<ImagePickerViewModel>()
        ImagePickerScreen(
            viewModel = imagePickerViewModel,
            maxSelectionCount = maxSelectionCount,
            onSelect = { images ->
                actions.setSelectedImages(images)
                actions.navigateUp()
            },
            onClose = { actions.navigateUp() }
        )
    }
}

class DiaryActions(private val navController: NavController) {

    val savedStateHandle: SavedStateHandle?
        get() = navController.currentBackStackEntry?.savedStateHandle

    fun navigateToImagePicker(maxSelectionCount: Int) {
        navController.navigate(
            "${DiaryDestinations.IMAGE_PICKER_ROUTE}/$maxSelectionCount"
        )
    }

    fun setSelectedImages(images: List<LocalImage>) {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set(SELECTED_IMAGE_URIS_RESULT_KEY, images)
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}