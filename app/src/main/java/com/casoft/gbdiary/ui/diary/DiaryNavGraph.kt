package com.casoft.gbdiary.ui.diary

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.casoft.gbdiary.ui.diary.image.ImagePickerScreen
import com.casoft.gbdiary.ui.diary.image.ImagePickerViewModel
import com.casoft.gbdiary.ui.diary.image.ImageViewerScreen
import com.casoft.gbdiary.ui.diary.image.SELECTED_IMAGES_RESULT_KEY
import java.io.File
import java.time.LocalDate

object DiaryDestinations {
    const val HOME_ROUTE = "diary/home"
    const val HOME_YEAR_KEY = "year"
    const val HOME_MONTH_KEY = "month"
    const val HOME_DAY_OF_MONTH_KEY = "dayOfMonth"
    const val IMAGE_PICKER_ROUTE = "diary/image/select"
    const val IMAGE_PICKER_PRE_SELECTION_COUNT_KEY = "preSelectionCount"
    const val IMAGE_VIEWER_ROUTE = "diary/image/view"
    const val IMAGE_VIEWER_INITIAL_PAGE_KEY = "initialPage"
    const val IMAGE_VIEWER_IMAGES_KEY = "images"
}

fun NavGraphBuilder.diaryNavGraph(actions: DiaryActions) {
    composable(
        route = "${DiaryDestinations.HOME_ROUTE}/{${DiaryDestinations.HOME_YEAR_KEY}}/{${DiaryDestinations.HOME_MONTH_KEY}}/{${DiaryDestinations.HOME_DAY_OF_MONTH_KEY}}",
        arguments = listOf(
            navArgument(DiaryDestinations.HOME_YEAR_KEY) { type = NavType.IntType },
            navArgument(DiaryDestinations.HOME_MONTH_KEY) { type = NavType.IntType },
            navArgument(DiaryDestinations.HOME_DAY_OF_MONTH_KEY) { type = NavType.IntType },
        )
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val year = arguments.getInt(DiaryDestinations.HOME_YEAR_KEY)
        val month = arguments.getInt(DiaryDestinations.HOME_MONTH_KEY)
        val dayOfMonth = arguments.getInt(DiaryDestinations.HOME_DAY_OF_MONTH_KEY)
        val diaryViewModel = hiltViewModel<DiaryViewModel>()
            .apply { setDate(LocalDate.of(year, month, dayOfMonth)) }

        DiaryScreen(
            viewModel = diaryViewModel,
            savedStateHandle = actions.savedStateHandle,
            onImageClick = { index ->
                actions.navigateToImageViewer(
                    images = diaryViewModel.images.value,
                    initialPage = index
                )
            },
            onAlbumClick = { preSelectionCount ->
                actions.navigateToImagePicker(preSelectionCount)
            },
            onBack = { actions.navigateUp() }
        )
    }
    composable(
        route = "${DiaryDestinations.IMAGE_PICKER_ROUTE}/{${DiaryDestinations.IMAGE_PICKER_PRE_SELECTION_COUNT_KEY}}",
        arguments = listOf(
            navArgument(DiaryDestinations.IMAGE_PICKER_PRE_SELECTION_COUNT_KEY) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val preSelectionCount = arguments.getInt(DiaryDestinations.IMAGE_PICKER_PRE_SELECTION_COUNT_KEY)

        val viewModel = hiltViewModel<ImagePickerViewModel>().apply {
            setPreSelectionCount(preSelectionCount)
        }

        ImagePickerScreen(
            viewModel = viewModel,
            onFinishSelection = { images ->
                actions.setSelectedImages(images)
                actions.navigateUp()
            },
            onClose = { actions.navigateUp() }
        )
    }
    composable(
        route = "${DiaryDestinations.IMAGE_VIEWER_ROUTE}/{${DiaryDestinations.IMAGE_VIEWER_INITIAL_PAGE_KEY}}",
        arguments = listOf(
            navArgument(DiaryDestinations.IMAGE_VIEWER_INITIAL_PAGE_KEY) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = requireNotNull(backStackEntry.arguments)
        val initialPage = arguments.getInt(DiaryDestinations.IMAGE_VIEWER_INITIAL_PAGE_KEY)
        val images = actions.getImagesForViewer()

        ImageViewerScreen(
            images = images,
            initialPage = initialPage,
            onClose = { actions.navigateUp() }
        )
    }
}

class DiaryActions(private val navController: NavController) {

    val savedStateHandle: SavedStateHandle?
        get() = navController.currentBackStackEntry?.savedStateHandle

    val images: List<File>
        get() = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get(DiaryDestinations.IMAGE_VIEWER_IMAGES_KEY)
            ?: listOf()

    fun navigateToImagePicker(preSelectionCount: Int) {
        navController.navigate(
            "${DiaryDestinations.IMAGE_PICKER_ROUTE}/$preSelectionCount"
        )
    }

    fun setSelectedImages(images: List<File>) {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set(SELECTED_IMAGES_RESULT_KEY, images)
    }

    fun navigateToImageViewer(images: List<File>, initialPage: Int) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set(DiaryDestinations.IMAGE_VIEWER_IMAGES_KEY, images)

        navController.navigate("${DiaryDestinations.IMAGE_VIEWER_ROUTE}/$initialPage")
    }

    fun getImagesForViewer(): List<File> {
        return navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get(DiaryDestinations.IMAGE_VIEWER_IMAGES_KEY)
            ?: listOf()
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}