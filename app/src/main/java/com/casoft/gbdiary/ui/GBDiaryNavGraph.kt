package com.casoft.gbdiary.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.casoft.gbdiary.ui.diary.DiaryActions
import com.casoft.gbdiary.ui.lock.ScreenLockScreen
import com.casoft.gbdiary.ui.lock.ScreenLockViewModel

@Composable
fun GBDiaryNavGraph(
    navController: NavHostController = rememberNavController(),
    screenLockEnabled: Boolean? = false,
    finishActivity: () -> Unit = {},
) {
    val mainActions = remember(navController) { MainActions(navController) }
    val diaryActions = remember(navController) { DiaryActions(navController) }

    val screenUnlocked = remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(screenLockEnabled) {
        if (screenLockEnabled != null) {
            screenUnlocked.value = !screenLockEnabled
        }
    }

    NavHost(
        navController = navController,
        startDestination = GBDiaryDestinations.MAIN_ROUTE,
    ) {
        navigation(
            route = GBDiaryDestinations.MAIN_ROUTE,
            startDestination = MainDestinations.HOME_ROUTE
        ) {
            mainNavGraph(
                navController = navController,
                actions = mainActions,
                diaryActions = diaryActions,
                screenUnlocked = screenUnlocked
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
                    screenUnlocked.value = true
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