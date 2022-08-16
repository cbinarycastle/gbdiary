package com.casoft.gbdiary.ui.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle

@Composable
fun <T> SavedStateHandle.CollectOnce(
    key: String,
    defaultValue: T,
    block: (T) -> Unit,
) {
    val value by getStateFlow(key, defaultValue).collectAsState()

    LaunchedEffect(value) {
        block(value)
        remove<T>(key)
    }
}