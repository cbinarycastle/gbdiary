package com.casoft.gbdiary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    navigateToCalendar: () -> Unit,
    navigateToSignIn: () -> Unit,
    navigateToBackup: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = navigateToCalendar) {
            Text("캘린더")
        }
        Button(onClick = navigateToSignIn) {
            Text("로그인")
        }
        Button(onClick = navigateToBackup) {
            Text("백업")
        }
    }
}