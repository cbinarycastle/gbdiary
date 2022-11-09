package com.casoft.gbdiary.ui.components

import androidx.compose.runtime.*
import com.casoft.gbdiary.ui.model.Message

@Composable
fun rememberAlertDialogState() = remember { AlertDialogState() }

@Stable
class AlertDialogState {
    var message by mutableStateOf<Message.AlertDialog?>(null)
}