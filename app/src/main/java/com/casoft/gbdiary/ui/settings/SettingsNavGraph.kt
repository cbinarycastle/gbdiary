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
            onPurchaseClick = actions::navigateToPurchase,
            onThemeClick = actions::navigateToTheme,
            onFontSizeClick = actions::navigateToFontSize,
            onScreenLockClick = actions::navigateToScreenLock,
            onBackupClick = actions::navigateToBackup,
            onBack = actions::navigateUp
        )
    }
    composable(SettingsDestination.PURCHASE_ROUTE) {
        val purchaseViewModel = hiltViewModel<PurchaseViewModel>()
        PurchaseScreen(
            viewModel = purchaseViewModel,
            onClose = actions::navigateUp
        )
    }
    composable(SettingsDestination.THEME_ROUTE) {
        val themeViewModel = hiltViewModel<ThemeViewModel>()
        ThemeScreen(
            viewModel = themeViewModel,
            onBack = actions::navigateUp
        )
    }
    composable(SettingsDestination.FONT_SIZE_ROUTE) {
        val fontSizeViewModel = hiltViewModel<FontSizeViewModel>()
        FontSizeScreen(
            viewModel = fontSizeViewModel,
            onBack = actions::navigateUp
        )
    }
    composable(SettingsDestination.SCREEN_LOCK_ROUTE) {
        val screenLockSettingViewModel = hiltViewModel<ScreenLockSettingViewModel>()
        ScreenLockSettingScreen(
            viewModel = screenLockSettingViewModel,
            navigateToPasswordRegistration = actions::navigateToPasswordRegistration,
            navigateToPasswordChange = actions::navigateToPasswordChange,
            onBack = actions::navigateUp
        )
    }
    composable(SettingsDestination.PASSWORD_REGISTRATION_ROUTE) {
        val passwordRegistrationViewModel = hiltViewModel<PasswordRegistrationViewModel>()
        PasswordRegistrationScreen(
            viewModel = passwordRegistrationViewModel,
            initialMessage = "비밀번호를 입력해주세요",
            onBack = actions::navigateUp
        )
    }
    composable(SettingsDestination.PASSWORD_CHANGE_ROUTE) {
        val passwordRegistrationViewModel = hiltViewModel<PasswordRegistrationViewModel>()
        PasswordRegistrationScreen(
            viewModel = passwordRegistrationViewModel,
            initialMessage = "새 비밀번호를 입력해주세요",
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

    fun navigateToPurchase() {
        navController.navigate(SettingsDestination.PURCHASE_ROUTE)
    }

    fun navigateToTheme() {
        navController.navigate(SettingsDestination.THEME_ROUTE)
    }

    fun navigateToFontSize() {
        navController.navigate(SettingsDestination.FONT_SIZE_ROUTE)
    }

    fun navigateToScreenLock() {
        navController.navigate(SettingsDestination.SCREEN_LOCK_ROUTE)
    }

    fun navigateToPasswordRegistration() {
        navController.navigate(SettingsDestination.PASSWORD_REGISTRATION_ROUTE)
    }

    fun navigateToPasswordChange() {
        navController.navigate(SettingsDestination.PASSWORD_CHANGE_ROUTE)
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
    const val PURCHASE_ROUTE = "settings/purchase"
    const val THEME_ROUTE = "settings/theme"
    const val FONT_SIZE_ROUTE = "settings/font_size"
    const val SCREEN_LOCK_ROUTE = "settings/screen_lock"
    const val PASSWORD_REGISTRATION_ROUTE = "settings/password_registration"
    const val PASSWORD_CHANGE_ROUTE = "settings/password_change"
    const val BACKUP_ROUTE = "settings/backup"
}