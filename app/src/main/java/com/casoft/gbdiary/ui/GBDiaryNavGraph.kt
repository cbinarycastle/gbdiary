package com.casoft.gbdiary.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.casoft.gbdiary.ui.MainDestinations.BACKUP_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.DIARY_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.HOME_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.SETTINGS_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.SIGN_IN_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.TIMELINE_MONTH_KEY
import com.casoft.gbdiary.ui.MainDestinations.TIMELINE_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.TIMELINE_YEAR_KEY
import com.casoft.gbdiary.ui.backup.BackupScreen
import com.casoft.gbdiary.ui.backup.BackupViewModel
import com.casoft.gbdiary.ui.calendar.CalendarScreen
import com.casoft.gbdiary.ui.calendar.CalendarViewModel
import com.casoft.gbdiary.ui.calendar.rememberCalendarState
import com.casoft.gbdiary.ui.diary.DiaryActions
import com.casoft.gbdiary.ui.diary.DiaryDestinations
import com.casoft.gbdiary.ui.diary.diaryNavGraph
import com.casoft.gbdiary.ui.settings.SettingsActions
import com.casoft.gbdiary.ui.settings.SettingsDestination
import com.casoft.gbdiary.ui.settings.settingsNavGraph
import com.casoft.gbdiary.ui.signin.SignInScreen
import com.casoft.gbdiary.ui.signin.SignInViewModel
import com.casoft.gbdiary.ui.timeline.TimelineScreen
import com.casoft.gbdiary.ui.timeline.TimelineViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun GBDiaryNavGraph(navController: NavHostController = rememberNavController()) {
    val actions = remember(navController) { MainActions(navController) }
    val diaryActions = remember(navController) { DiaryActions(navController) }

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
    ) {
        composable(HOME_ROUTE) {
            val calendarViewModel = hiltViewModel<CalendarViewModel>()
            CalendarScreen(
                viewModel = calendarViewModel,
                onDayClick = actions::navigateToDiary,
                onTimelineClick = { actions.navigateToTimeline(calendarViewModel.currentYearMonth) },
                onSettingsClick = actions::navigateToSettings,
                onWriteClick = actions::navigateToDiary,
                state = rememberCalendarState(calendarViewModel.currentYearMonth)
            )
        }
        navigation(
            route = DIARY_ROUTE,
            startDestination = DiaryDestinations.HOME_ROUTE
        ) {
            diaryNavGraph(diaryActions)
        }
        composable(
            route = "$TIMELINE_ROUTE/{$TIMELINE_YEAR_KEY}/{$TIMELINE_MONTH_KEY}",
            arguments = listOf(
                navArgument(TIMELINE_YEAR_KEY) { type = NavType.IntType },
                navArgument(TIMELINE_MONTH_KEY) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val year = arguments.getInt(TIMELINE_YEAR_KEY)
            val month = arguments.getInt(TIMELINE_MONTH_KEY)
            val timelineViewModel = hiltViewModel<TimelineViewModel>()

            timelineViewModel.moveToYearMonth(YearMonth.of(year, month))
            TimelineScreen(
                viewModel = timelineViewModel,
                onDiaryClick = actions::navigateToDiary,
                onBack = actions::navigateUp
            )
        }
        navigation(
            route = SETTINGS_ROUTE,
            startDestination = SettingsDestination.HOME_ROUTE
        ) {
            settingsNavGraph(actions = SettingsActions(navController))
        }
        composable(SIGN_IN_ROUTE) {
            val signInViewModel = hiltViewModel<SignInViewModel>()
            SignInScreen(viewModel = signInViewModel)
        }
        composable(BACKUP_ROUTE) {
            val backupViewModel = hiltViewModel<BackupViewModel>()
            BackupScreen(viewModel = backupViewModel)
        }
    }
}

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val DIARY_ROUTE = "diary"
    const val TIMELINE_ROUTE = "timeline"
    const val TIMELINE_YEAR_KEY = "year"
    const val TIMELINE_MONTH_KEY = "month"
    const val SETTINGS_ROUTE = "settings"
    const val SIGN_IN_ROUTE = "signIn"
    const val BACKUP_ROUTE = "backup"
}

class MainActions(private val navController: NavHostController) {

    fun navigateToDiary(date: LocalDate) {
        navController.navigate(
            "${DiaryDestinations.HOME_ROUTE}/${date.year}/${date.monthValue}/${date.dayOfMonth}"
        )
    }

    fun navigateToTimeline(initialYearMonth: YearMonth) {
        navController.navigate("$TIMELINE_ROUTE/${initialYearMonth.year}/${initialYearMonth.monthValue}")
    }

    fun navigateToSettings() {
        navController.navigate(SETTINGS_ROUTE)
    }

    fun navigateToSignIn() {
        navController.navigate(SIGN_IN_ROUTE)
    }

    fun navigateToBackup() {
        navController.navigate(BACKUP_ROUTE)
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}