package com.casoft.gbdiary.ui.diary

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.casoft.gbdiary.ui.components.GBDiaryAlertDialog

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GBDiaryAlertDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        message = { Text("정말로 일기를 삭제할까요?") },
        confirmText = { Text("삭제") },
        dismissText = { Text("취소") }
    )
}