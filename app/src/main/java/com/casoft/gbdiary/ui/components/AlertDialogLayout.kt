package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AlertDialogLayout(
    modifier: Modifier = Modifier,
    state: AlertDialogState = rememberAlertDialogState(),
    onConfirm: () -> Unit = { state.message = null },
    onDismiss: () -> Unit = { state.message = null },
    content: @Composable () -> Unit,
) {
    Box(modifier.fillMaxSize()) {
        content()
        state.message?.let { message ->
            GBDiaryAlertDialog(
                onConfirm = onConfirm,
                onDismiss = onDismiss,
                content = {
                    Text(
                        text = message.text,
                        textAlign = TextAlign.Center
                    )
                },
                confirmText = { Text(message.confirmText) },
                dismissText = message.dismissText?.let { dismissText ->
                    { Text(dismissText) }
                }
            )
        }
    }
}