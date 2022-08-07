package com.casoft.gbdiary.ui.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object SettingsDestination {

    const val HOME = "settings/home"
}

fun NavGraphBuilder.addSettingsGraph(onBack: () -> Unit) {
    composable(SettingsDestination.HOME) {
        SettingsScreen(onBack = onBack)
    }
}