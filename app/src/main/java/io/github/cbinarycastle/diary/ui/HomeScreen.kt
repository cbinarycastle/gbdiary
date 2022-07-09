package io.github.cbinarycastle.diary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(navigateToSignIn: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Button(
            onClick = navigateToSignIn,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("로그인")
        }
    }
}