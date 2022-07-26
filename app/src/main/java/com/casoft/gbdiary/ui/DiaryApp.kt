package com.casoft.gbdiary.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.casoft.gbdiary.ui.signin.SignInScreen
import com.casoft.gbdiary.ui.signin.SignInViewModel

@Composable
fun DiaryApp() {
    val navController = rememberNavController()

    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = MainDestination.HOME,
        ) {
            composable(MainDestination.HOME) {
                HomeScreen(
                    navigateToSignIn = { navController.navigate(MainDestination.SIGN_IN) }
                )
            }
            composable(MainDestination.SIGN_IN) {
                val signInViewModel = hiltViewModel<SignInViewModel>()
                SignInScreen(viewModel = signInViewModel)
            }
        }
    }
}