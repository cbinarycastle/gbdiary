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
import com.casoft.gbdiary.ui.MainDestinations.CALENDAR_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.HOME_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.SETTINGS_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.SIGN_IN_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.WRITE_DAY_OF_MONTH_KEY
import com.casoft.gbdiary.ui.MainDestinations.WRITE_MONTH_KEY
import com.casoft.gbdiary.ui.MainDestinations.WRITE_ROUTE
import com.casoft.gbdiary.ui.MainDestinations.WRITE_YEAR_KEY
import com.casoft.gbdiary.ui.backup.BackupScreen
import com.casoft.gbdiary.ui.backup.BackupViewModel
import com.casoft.gbdiary.ui.calendar.CalendarScreen
import com.casoft.gbdiary.ui.calendar.CalendarViewModel
import com.casoft.gbdiary.ui.diary.DiaryScreen
import com.casoft.gbdiary.ui.diary.DiaryViewModel
import com.casoft.gbdiary.ui.settings.SettingsDestination
import com.casoft.gbdiary.ui.settings.settingsNavGraph
import com.casoft.gbdiary.ui.signin.SignInScreen
import com.casoft.gbdiary.ui.signin.SignInViewModel
import org.threeten.bp.LocalDate

@Composable
fun GBDiaryNavGraph(navController: NavHostController = rememberNavController()) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
    ) {
        composable(HOME_ROUTE) {
            HomeScreen(
                navigateToCalendar = { actions.navigateToCalendar() },
                navigateToSignIn = { actions.navigateToSignIn() },
                navigateToBackup = { actions.navigateToBackup() }
            )
        }
        composable(CALENDAR_ROUTE) {
            val calendarViewModel = hiltViewModel<CalendarViewModel>()
            CalendarScreen(
                viewModel = calendarViewModel,
                onDayClick = { actions.navigateToWrite(date = it) },
                onSettingsClick = { actions.navigateToSettings() },
                onWriteClick = { actions.navigateToWrite(date = it) }
            )
        }
        composable(
            route = "$WRITE_ROUTE/{$WRITE_YEAR_KEY}/{$WRITE_MONTH_KEY}/{$WRITE_DAY_OF_MONTH_KEY}",
            arguments = listOf(
                navArgument(WRITE_YEAR_KEY) { type = NavType.IntType },
                navArgument(WRITE_MONTH_KEY) { type = NavType.IntType },
                navArgument(WRITE_DAY_OF_MONTH_KEY) { type = NavType.IntType },
            )
        ) {
            val arguments = requireNotNull(it.arguments)
            val year = arguments.getInt(WRITE_YEAR_KEY)
            val month = arguments.getInt(WRITE_MONTH_KEY)
            val dayOfMonth = arguments.getInt(WRITE_DAY_OF_MONTH_KEY)
            val diaryViewModel = hiltViewModel<DiaryViewModel>()
            DiaryScreen(
                viewModel = diaryViewModel,
                date = LocalDate.of(year, month, dayOfMonth),
                onBack = { actions.navigateUp() }
            )
        }
        navigation(
            route = SETTINGS_ROUTE,
            startDestination = SettingsDestination.HOME_ROUTE
        ) {
            settingsNavGraph(
                onBack = { actions.navigateUp() }
            )
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
    const val CALENDAR_ROUTE = "calendar"
    const val WRITE_ROUTE = "write"
    const val WRITE_YEAR_KEY = "year"
    const val WRITE_MONTH_KEY = "month"
    const val WRITE_DAY_OF_MONTH_KEY = "dayOfMonth"
    const val SETTINGS_ROUTE = "settings"
    const val SIGN_IN_ROUTE = "signIn"
    const val BACKUP_ROUTE = "backup"
}

class MainActions(private val navController: NavHostController) {

    fun navigateToCalendar() {
        navController.navigate(CALENDAR_ROUTE)
    }

    fun navigateToWrite(date: LocalDate) {
        navController.navigate(
            "$WRITE_ROUTE/${date.year}/${date.monthValue}/${date.dayOfMonth}"
        )
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