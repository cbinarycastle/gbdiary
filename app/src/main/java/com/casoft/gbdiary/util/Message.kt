package com.casoft.gbdiary.util

import android.content.Context
import com.casoft.gbdiary.ui.components.AlertDialogState
import com.casoft.gbdiary.ui.model.Message
import kotlinx.coroutines.flow.Flow

suspend fun Flow<Message>.collectMessage(
    context: Context,
    alertDialogState: AlertDialogState,
) {
    collect {
        when (it) {
            is Message.Toast -> context.toast(it.text)
            is Message.AlertDialog -> alertDialogState.message = it
        }
    }
}