package com.casoft.gbdiary.ui.backup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.casoft.gbdiary.extensions.toast
import kotlinx.coroutines.launch

@Composable
fun BackupScreen(viewModel: BackupViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        coroutineScope.launch {
            viewModel.message.collect {
                context.toast(it)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Button(
            onClick = { viewModel.backup() },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("백업")
        }
    }
}