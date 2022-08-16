package com.casoft.gbdiary.ui.backup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.casoft.gbdiary.util.toast
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { viewModel.backup() }) {
            Text("백업")
        }
        Button(onClick = { viewModel.sync() }) {
            Text("복원")
        }
    }
}