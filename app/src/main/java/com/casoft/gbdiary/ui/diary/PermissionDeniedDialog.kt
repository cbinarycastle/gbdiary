package com.casoft.gbdiary.ui.diary

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.casoft.gbdiary.ui.components.GBDiaryAlertDialog

@Composable
fun PermissionDeniedDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GBDiaryAlertDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        message = { Text("사진 업로드를 위한 접근 권한 변경이 필요합니다.") },
        confirmText = { Text("설정") },
        dismissText = { Text("취소") }
    )
}