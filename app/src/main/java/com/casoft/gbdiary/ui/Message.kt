package com.casoft.gbdiary.ui

sealed interface Message {

    data class ToastMessage(
        val text: String,
        val longDuration: Boolean = false,
    ) : Message

    data class AlertDialogMessage(
        val text: String,
        val confirmText: String,
        val dismissText: String? = null,
    ) : Message
}