package com.casoft.gbdiary.ui.settings

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ad.SETTING_BANNER_AD_UNIT_ID
import com.casoft.gbdiary.ui.components.AdBanner
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.GBDiarySwitch
import com.casoft.gbdiary.ui.components.TimePickerDialog
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onThemeClick: () -> Unit,
    onBackupClick: () -> Unit,
    onBack: () -> Unit,
) {
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()

    SettingsScreen(
        notificationEnabled = notificationEnabled,
        notificationTime = notificationTime,
        onNotificationEnabledChange = viewModel::setNotificationEnabled,
        onNotificationTimeChange = viewModel::setNotificationTime,
        onThemeClick = onThemeClick,
        onBackupClick = onBackupClick,
        onBack = onBack
    )
}

@Composable
private fun SettingsScreen(
    notificationEnabled: Boolean,
    notificationTime: LocalTime?,
    onNotificationEnabledChange: (Boolean) -> Unit,
    onNotificationTimeChange: (LocalTime) -> Unit,
    onThemeClick: () -> Unit,
    onBackupClick: () -> Unit,
    onBack: () -> Unit,
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            AppBar(onBack = onBack)
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                PurchaseButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(16.dp))
                NotificationItem(
                    checked = notificationEnabled,
                    onCheckedChange = onNotificationEnabledChange
                )
                if (notificationTime != null) {
                    NotificationTimeItem(
                        time = notificationTime,
                        onClick = { showTimePickerDialog = true }
                    )
                }
                ThemeItem(onClick = onThemeClick)
                BackupItem(onClick = onBackupClick)
                Divider(Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                ReviewItem()
            }
            AdBanner(SETTING_BANNER_AD_UNIT_ID)
        }

        if (showTimePickerDialog && notificationTime != null) {
            TimePickerDialog(
                initialTime = notificationTime,
                onConfirm = { time ->
                    showTimePickerDialog = false
                    onNotificationTimeChange(time)
                },
                onDismiss = { showTimePickerDialog = false }
            )
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
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.8.dp,
            color = GBDiaryTheme.gbDiaryColors.border
        ),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { /*TODO*/ }
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "프리미엄 이용권 구매",
                style = GBDiaryTheme.typography.subtitle1,
                modifier = Modifier.padding(start = 36.dp)
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
        name = Settings.NOTIFICATION.text,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = Settings.NOTIFICATION_TIME.text,
        icon = painterResource(R.drawable.time),
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = time.format(DateTimeFormatter.ofPattern("a hh:mm")),
            modifier = Modifier.alpha(0.4f)
        )
    }
}

@Composable
private fun ThemeItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = Settings.THEME.text,
        icon = painterResource(R.drawable.theme),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun BackupItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = Settings.BACKUP.text,
        icon = painterResource(R.drawable.data),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun ReviewItem(modifier: Modifier = Modifier) {
    SettingsItem(
        name = Settings.REVIEW.text,
        icon = painterResource(R.drawable.app_review),
        modifier = modifier
    )
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
            onNotificationTimeChange = {},
            onThemeClick = {},
            onBackupClick = {},
            onBack = {}
        )
    }
}