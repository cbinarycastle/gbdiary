package com.casoft.gbdiary.ui

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.casoft.gbdiary.ui.backup.BackupScreen
import com.casoft.gbdiary.ui.backup.BackupViewModel
import com.casoft.gbdiary.ui.calendar.CalendarScreen
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
                composable(MainDestination.CALENDAR) {
                    CalendarScreen()
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