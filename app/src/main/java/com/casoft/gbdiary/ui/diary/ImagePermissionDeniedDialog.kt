package com.casoft.gbdiary.ui.diary

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.casoft.gbdiary.ui.components.GBDiaryAlertDialog

@Composable
fun ImagePermissionDeniedDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GBDiaryAlertDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        content = { Text("사진을 업로드하려면 사진 접근 권한을 허용해야 합니다.") },
        confirmText = { Text("설정") },
        dismissText = { Text("취소") }
    )
}