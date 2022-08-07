package com.casoft.gbdiary.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.casoft.gbdiary.ui.backup.BackupScreen
import com.casoft.gbdiary.ui.backup.BackupViewModel
import com.casoft.gbdiary.ui.calendar.CalendarScreen
import com.casoft.gbdiary.ui.calendar.CalendarViewModel
import com.casoft.gbdiary.ui.settings.SettingsDestination
import com.casoft.gbdiary.ui.settings.addSettingsGraph
import com.casoft.gbdiary.ui.signin.SignInScreen
import com.casoft.gbdiary.ui.signin.SignInViewModel
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

@Composable
fun DiaryApp() {
    val navController = rememberNavController()

    GBDiaryTheme {
        Surface(color = GBDiaryTheme.colors.background) {
            NavHost(
                navController = navController,
                startDestination = MainDestination.HOME,
            ) {
                composable(MainDestination.HOME) {
                    HomeScreen(
                        navigateToCalendar = { navController.navigate(MainDestination.CALENDAR) },
                        navigateToSignIn = { navController.navigate(MainDestination.SIGN_IN) },
                        navigateToBackup = { navController.navigate(MainDestination.BACKUP) }
                    )
                }
                navigation(
                    route = MainDestination.SETTINGS,
                    startDestination = SettingsDestination.HOME
                ) {
                    addSettingsGraph(
                        onBack = { navController.navigateUp() }
                    )
                }
                composable(MainDestination.CALENDAR) {
                    val calendarViewModel = hiltViewModel<CalendarViewModel>()
                    CalendarScreen(
                        viewModel = calendarViewModel,
                        onSettingsClick = { navController.navigate(MainDestination.SETTINGS) }
                    )
                }
                composable(MainDestination.SIGN_IN) {
                    val signInViewModel = hiltViewModel<SignInViewModel>()
                    SignInScreen(viewModel = signInViewModel)
                }
                composable(MainDestination.BACKUP) {
                    val backupViewModel = hiltViewModel<BackupViewModel>()
                    BackupScreen(viewModel = backupViewModel)
                }
            }
        }
    }
}