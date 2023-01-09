package com.casoft.gbdiary.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.casoft.gbdiary.ui.diary.DiaryActions
import com.casoft.gbdiary.ui.lock.ScreenLockScreen
import com.casoft.gbdiary.ui.lock.ScreenLockViewModel

@Composable
fun GBDiaryNavGraph(
    navController: NavHostController = rememberNavController(),
    shouldLockScreen: Boolean?,
    onUnlockScreen: () -> Unit = {},
    finishActivity: () -> Unit = {},
) {
    val mainActions = remember(navController) { MainActions(navController) }
    val diaryActions = remember(navController) { DiaryActions(navController) }

    val shouldLockScreenState = rememberUpdatedState(shouldLockScreen)

    if (shouldLockScreen != null) {
        LaunchedEffect(shouldLockScreen) {
            if (shouldLockScreen) {
                navController.navigate(GBDiaryDestinations.SCREEN_LOCK_ROUTE)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = GBDiaryDestinations.MAIN_ROUTE,
    ) {
        navigation(
            route = GBDiaryDestinations.MAIN_ROUTE,
            startDestination = MainDestinations.HOME_ROUTE,
        ) {
            mainNavGraph(
                navController = navController,
                shouldLockScreen = shouldLockScreenState,
                actions = mainActions,
                diaryActions = diaryActions
            )
        }
        composable(GBDiaryDestinations.SCREEN_LOCK_ROUTE) {
            BackHandler {
                finishActivity()
            }

            val screenLockViewModel = hiltViewModel<ScreenLockViewModel>()
            ScreenLockScreen(
                viewModel = screenLockViewModel,
                onUnlock = {
                    onUnlockScreen()
                    navController.popBackStack()
                },
                onClose = finishActivity
            )
        }
    }
}

object GBDiaryDestinations {
    const val MAIN_ROUTE = "main"
    const val SCREEN_LOCK_ROUTE = "screen_lock"
}