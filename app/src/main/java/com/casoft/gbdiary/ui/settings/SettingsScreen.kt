package com.casoft.gbdiary.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.GBDiaryAppBar
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

private val HorizontalPadding = 24.dp

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        AppBar(onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth()) {
            PurchaseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HorizontalPadding)
            )
            Spacer(Modifier.height(16.dp))
            NotificationItem(Modifier.fillMaxWidth())
            ThemeItem(Modifier.fillMaxWidth())
            BackupItem(Modifier.fillMaxWidth())
            Divider(Modifier.padding(horizontal = HorizontalPadding, vertical = 16.dp))
            ReviewItem(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun AppBar(onBack: () -> Unit) {
    GBDiaryAppBar {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(R.drawable.back),
                contentDescription = "뒤로"
            )
        }
    }
}

@Composable
private fun PurchaseButton(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.8.dp,
            color = GBDiaryTheme.gbDiaryColors.border
        )
    ) {
        Row(
            modifier = Modifier
                .clickable { }
                .padding(vertical = 4.dp),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Text(
                text = "프리미엄 이용권 구매",
                modifier = Modifier.padding(start = 36.dp),
                style = GBDiaryTheme.typography.subtitle1
            )
            Image(
                painter = painterResource(R.drawable.satisfaction),
                contentDescription = "프리미엄 이용권 구매",
                modifier = Modifier
                    .size(72.dp)
                    .padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun NotificationItem(modifier: Modifier = Modifier) {
    var checked by remember { mutableStateOf(false) }

    SettingsItem(
        name = "일기 알림",
        icon = painterResource(R.drawable.notification),
        modifier = modifier
    ) {
        GBDiarySwitch(
            checked = checked,
            onCheckedChange = { checked = !checked }
        )
    }
}

@Composable
private fun ThemeItem(modifier: Modifier = Modifier) {
    SettingsItem(
        name = "테마 설정",
        icon = painterResource(R.drawable.theme),
        modifier = modifier
    )
}

@Composable
private fun BackupItem(modifier: Modifier = Modifier) {
    SettingsItem(
        name = "백업/복원",
        icon = painterResource(R.drawable.data),
        modifier = modifier
    )
}

@Composable
private fun ReviewItem(modifier: Modifier = Modifier) {
    SettingsItem(
        name = "앱 평가하기",
        icon = painterResource(R.drawable.app_review),
        modifier = modifier
    )
}

@Composable
private fun SettingsItem(
    name: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    widget: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier.padding(horizontal = HorizontalPadding),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = CenterVertically
    ) {
        Row {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.align(CenterVertically)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = name,
                modifier = Modifier.padding(top = 8.dp, bottom = 15.dp),
                style = GBDiaryTheme.typography.subtitle1
            )
        }
        widget()
    }
}