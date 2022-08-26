package com.casoft.gbdiary.ui.settings

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.settingsNavGraph(actions: SettingsActions) {
    composable(SettingsDestination.HOME_ROUTE) {
        val settingsViewModel = hiltViewModel<SettingsViewModel>()
        SettingsScreen(
            viewModel = settingsViewModel,
            onThemeClick = actions::navigateToTheme,
            onBackupClick = actions::navigateToBackup,
            onBack = actions::navigateUp
        )
    }
    composable(SettingsDestination.THEME_ROUTE) {
        val themeViewModel = hiltViewModel<ThemeViewModel>()
        ThemeScreen(
            viewModel = themeViewModel,
            onBack = actions::navigateUp
        )
    }
    composable(SettingsDestination.BACKUP_ROUTE) {
        val backupViewModel = hiltViewModel<BackupViewModel>()
        BackupScreen(
            viewModel = backupViewModel,
            onBack = actions::navigateUp
        )
    }
}

class SettingsActions(private val navController: NavHostController) {

    fun navigateToTheme() {
        navController.navigate(SettingsDestination.THEME_ROUTE)
    }

    fun navigateToBackup() {
        navController.navigate(SettingsDestination.BACKUP_ROUTE)
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}

object SettingsDestination {
    const val HOME_ROUTE = "settings/home"
    const val THEME_ROUTE = "settings/theme"
    const val BACKUP_ROUTE = "settings/backup"
}