package com.casoft.gbdiary.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.casoft.gbdiary.ui.extension.border
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

@Composable
fun GBDiaryDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    message: @Composable () -> Unit,
    confirmText: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: (@Composable () -> Unit)? = null,
) {
    val borderColor = GBDiaryTheme.colors.onSurface

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 112.dp)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    message()
                }
                Row {
                    if (dismissText != null) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDismiss() }
                                .border(
                                    color = borderColor,
                                    top = 1.dp,
                                    alpha = 0.05f
                                )
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            dismissText()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onConfirm() }
                            .border(
                                color = borderColor,
                                top = 1.dp,
                                alpha = 0.05f
                            )
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        confirmText()
                    }
                }
            }
        }
    }
}