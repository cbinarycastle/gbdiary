package com.casoft.gbdiary.ui.settings

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.ActivityNotFoundException
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ad.SETTING_BANNER_AD_UNIT_ID
import com.casoft.gbdiary.ui.components.*
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onPurchaseClick: () -> Unit,
    onThemeClick: () -> Unit,
    onFontSizeClick: () -> Unit,
    onScreenLockClick: () -> Unit,
    onBackupClick: () -> Unit,
    onBack: () -> Unit,
) {
    val alertDialogState = rememberAlertDialogState()

    val context = LocalContext.current

    val isPremiumUser by viewModel.isPremiumUser.collectAsState()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val notificationTime by viewModel.notificationTime.collectAsState()

    var notificationPermissionDialogVisible by remember { mutableStateOf(false) }
    var scheduleExactAlarmRequestDialogVisible by remember { mutableStateOf(false) }

    val notificationPermissionState: PermissionState? = if (Build.VERSION.SDK_INT >= TIRAMISU) {
        rememberPermissionState(POST_NOTIFICATIONS) { granted ->
            if (granted) {
                if (context.canScheduleExactAlarm()) {
                    viewModel.setNotificationEnabled(true)
                } else {
                    scheduleExactAlarmRequestDialogVisible = true
                }
            } else {
                notificationPermissionDialogVisible = true
            }
        }
    } else {
        null
    }

    LaunchedEffect(viewModel) {
        viewModel.message.collectMessage(context, alertDialogState)
    }

    Box(Modifier.fillMaxSize()) {
        SettingsScreen(
            alertDialogState = alertDialogState,
            isPremiumUser = isPremiumUser,
            notificationEnabled = notificationEnabled,
            notificationTime = notificationTime,
            onNotificationEnabledChange = { enabled ->
                if (enabled) {
                    val notificationPermissionGranted = notificationPermissionState?.status?.isGranted ?: true
                    if (notificationPermissionGranted) {
                        if (context.canScheduleExactAlarm()) {
                            viewModel.setNotificationEnabled(true)
                        } else {
                            scheduleExactAlarmRequestDialogVisible = true
                        }
                    } else {
                        notificationPermissionState?.launchPermissionRequest()
                    }
                } else {
                    viewModel.setNotificationEnabled(false)
                }
            },
            onNotificationTimeChange = viewModel::setNotificationTime,
            onPurchaseClick = onPurchaseClick,
            onThemeClick = onThemeClick,
            onFontSizeClick = onFontSizeClick,
            onBackupClick = onBackupClick,
            onScreenLockClick = onScreenLockClick,
            onReviewClick = {
                try {
                    context.navigateToGooglePlay()
                } catch (e: ActivityNotFoundException) {
                    context.toast("구글 플레이 스토어가 존재하지 않습니다.")
                }
            },
            onBack = onBack
        )

        if (notificationPermissionDialogVisible) {
            NotificationPermissionDialog(
                onConfirm = {
                    notificationPermissionDialogVisible = false
                    context.navigateToAppSettings()
                },
                onDismiss = { notificationPermissionDialogVisible = false }
            )
        }

        if (scheduleExactAlarmRequestDialogVisible) {
            ScheduleExactAlarmRequestDialog(
                onConfirm = {
                    scheduleExactAlarmRequestDialogVisible = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        context.requestScheduleExactAlarm()
                    }
                },
                onDismiss = { scheduleExactAlarmRequestDialogVisible = false }
            )
        }
    }
}

@Composable
private fun SettingsScreen(
    alertDialogState: AlertDialogState,
    isPremiumUser: Boolean,
    notificationEnabled: Boolean,
    notificationTime: LocalTime?,
    onNotificationEnabledChange: (Boolean) -> Unit,
    onNotificationTimeChange: (LocalTime) -> Unit,
    onPurchaseClick: () -> Unit,
    onThemeClick: () -> Unit,
    onFontSizeClick: () -> Unit,
    onScreenLockClick: () -> Unit,
    onBackupClick: () -> Unit,
    onReviewClick: () -> Unit,
    onBack: () -> Unit,
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }

    AlertDialogLayout(state = alertDialogState) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                AppBar(onBack = onBack)
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    PurchaseButton(
                        onClick = onPurchaseClick,
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
                    FontSizeItem(onClick = onFontSizeClick)
                    ItemDivider()
                    ScreenLockItem(onClick = onScreenLockClick)
                    BackupItem(onClick = onBackupClick)
                    ItemDivider()
                    ReviewItem(onClick = onReviewClick)
                }
                if (isPremiumUser.not()) {
                    AdBanner(SETTING_BANNER_AD_UNIT_ID)
                }
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
private fun PurchaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                .clickable { onClick() }
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "프리미엄 이용권 구매",
                style = GBDiaryTheme.typography.subtitle2,
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
private fun FontSizeItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = Settings.FONT_SIZE.text,
        icon = painterResource(R.drawable.font),
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
private fun ScreenLockItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = Settings.SCREEN_LOCK.text,
        icon = painterResource(R.drawable.lock),
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun ReviewItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsItem(
        name = Settings.REVIEW.text,
        icon = painterResource(R.drawable.app_review),
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun ItemDivider() {
    Divider(Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
}

@Composable
private fun NotificationPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GBDiaryAlertDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        content = { Text("알림을 설정하려면 알림 권한을 허용해야 합니다.") },
        confirmText = { Text("설정") },
        dismissText = { Text("취소") }
    )
}

@Composable
private fun ScheduleExactAlarmRequestDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    GBDiaryAlertDialog(
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        content = { Text("정확한 알림을 위해 알람 및 리마인더 설정을 허용해야 합니다.") },
        confirmText = { Text("설정") },
        dismissText = { Text("취소") }
    )
}

@Preview(name = "Settings screen")
@Preview(name = "Settings screen (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview() {
    GBDiaryTheme {
        SettingsScreen(
            alertDialogState = rememberAlertDialogState(),
            isPremiumUser = false,
            notificationEnabled = false,
            notificationTime = LocalTime.of(22, 0),
            onNotificationEnabledChange = {},
            onNotificationTimeChange = {},
            onPurchaseClick = {},
            onThemeClick = {},
            onFontSizeClick = {},
            onScreenLockClick = {},
            onBackupClick = {},
            onReviewClick = {},
            onBack = {}
        )
    }
}