package com.casoft.gbdiary.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

@Composable
fun SettingsItem(
    name: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp),
    widget: @Composable () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(43.dp)
            .then(
                if (onClick == null) {
                    Modifier
                } else {
                    Modifier.clickable { onClick() }
                }
            )
            .padding(contentPadding)
    ) {
        ProvideTextStyle(GBDiaryTheme.typography.subtitle1) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        painter = icon,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Text(
                    text = name,
                    style = GBDiaryTheme.typography.subtitle1
                )
            }
            widget()
        }
    }
}