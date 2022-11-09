package com.casoft.gbdiary.ui.model

sealed interface Message {

    data class Toast(
        val text: String,
        val longDuration: Boolean = false,
    ) : Message

    data class AlertDialog(
        val text: String,
        val confirmText: String,
        val dismissText: String? = null,
    ) : Message
}