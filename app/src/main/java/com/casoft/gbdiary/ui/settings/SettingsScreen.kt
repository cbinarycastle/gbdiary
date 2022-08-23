package com.casoft.gbdiary.ui.settings

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val HorizontalPadding = 24.dp

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()

    SettingsScreen(
        notificationEnabled = notificationEnabled,
        notificationTime = notificationTime,
        onNotificationEnabledChange = viewModel::setNotificationEnabled,
        onBack = onBack
    )
}

@Composable
private fun SettingsScreen(
    notificationEnabled: Boolean,
    notificationTime: LocalTime?,
    onNotificationEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppBar(onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth()) {
            PurchaseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HorizontalPadding)
            )
            Spacer(Modifier.height(16.dp))
            NotificationItem(
                checked = notificationEnabled,
                onCheckedChange = onNotificationEnabledChange
            )
            if (notificationTime != null) {
                NotificationTimeItem(notificationTime)
            }
            ThemeItem()
            BackupItem()
            Divider(Modifier.padding(horizontal = HorizontalPadding, vertical = 16.dp))
            ReviewItem()
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
private fun NotificationItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = "일기 알림",
        icon = painterResource(R.drawable.notification),
        modifier = modifier
    ) {
        GBDiarySwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun NotificationTimeItem(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = "알림 시간",
        icon = painterResource(R.drawable.time),
        modifier = modifier
    ) {
        Text(
            text = time.format(DateTimeFormatter.ofPattern("a hh:mm")),
            modifier = Modifier.alpha(0.4f)
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
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = CenterVertically
    ) {
        ProvideTextStyle(GBDiaryTheme.typography.subtitle1) {
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
}

@Preview(name = "Settings screen")
@Preview(name = "Settings screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview() {
    GBDiaryTheme {
        SettingsScreen(
            notificationEnabled = false,
            notificationTime = LocalTime.of(22, 0),
            onNotificationEnabledChange = {},
            onBack = {}
        )
    }
}