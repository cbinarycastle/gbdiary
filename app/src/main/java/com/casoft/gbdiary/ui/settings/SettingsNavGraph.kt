package com.casoft.gbdiary.ui.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object SettingsDestination {
    const val HOME_ROUTE = "settings/home"
}

fun NavGraphBuilder.settingsNavGraph(onBack: () -> Unit) {
    composable(SettingsDestination.HOME_ROUTE) {
        SettingsScreen(onBack = onBack)
    }
}