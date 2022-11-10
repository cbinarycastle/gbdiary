package com.casoft.gbdiary.ui.settings

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.components.*
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.collectMessage
import com.casoft.gbdiary.util.toast
import kotlinx.coroutines.launch

@Composable
fun ScreenLockSettingScreen(
    viewModel: ScreenLockSettingViewModel,
    navigateToPasswordRegistration: () -> Unit,
    navigateToPasswordChange: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    val alertDialogState = rememberAlertDialogState()

    val passwordLockEnabled by viewModel.passwordLockEnabled.collectAsState()
    val biometricsLockEnabled by viewModel.biometricsLockEnabled.collectAsState()

    var showBiometricEnrollDialog by remember { mutableStateOf(false) }

    val biometricEnrollLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        if (it.resultCode >= Activity.RESULT_FIRST_USER) {
            viewModel.enableBiometricsLock()
            context.toast("생체 인증 잠금이 설정되었습니다.")
        }
    }

    LaunchedEffect(viewModel) {
        launch {
            viewModel.message.collectMessage(context, alertDialogState)
        }

        launch {
            viewModel.shouldEnrollBiometric.collect {
                showBiometricEnrollDialog = true
            }
        }
    }

    AlertDialogLayout(state = alertDialogState) {
        Box(Modifier.fillMaxSize()) {
            ScreenLockSettingScreen(
                passwordLockEnabled = passwordLockEnabled,
                onPasswordLockEnabledChange = { enabled ->
                    if (enabled) {
                        navigateToPasswordRegistration()
                    } else {
                        viewModel.disablePasswordLock()
                    }
                },
                onPasswordChangeClick = navigateToPasswordChange,
                biometricsLockEnabled = biometricsLockEnabled,
                onBiometricsLockEnabledChange = { enabled ->
                    if (enabled) {
                        viewModel.checkBiometricsLockAvailable()
                    } else {
                        viewModel.disableBiometricsLock()
                    }
                },
                onBack = onBack
            )

            if (showBiometricEnrollDialog) {
                GBDiaryAlertDialog(
                    onConfirm = {
                        biometricEnrollLauncher.launch(viewModel.biometricEnrollIntent)
                        showBiometricEnrollDialog = false
                    },
                    onDismiss = { showBiometricEnrollDialog = false },
                    content = { Text("생체 인증 정보 등록이 필요합니다.") },
                    confirmText = { Text("설정 이동") },
                    dismissText = { Text("취소") }
                )
            }
        }
    }
}

@Composable
private fun ScreenLockSettingScreen(
    passwordLockEnabled: Boolean,
    onPasswordLockEnabledChange: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    biometricsLockEnabled: Boolean,
    onBiometricsLockEnabledChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        AppBar(onBack)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "비밀번호를 잃어버리면 찾을 수 없습니다.",
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .alpha(0.4f)
        )
        Spacer(Modifier.height(40.dp))
        PasswordItem(
            enabled = passwordLockEnabled,
            onEnabledChange = onPasswordLockEnabledChange
        )
        if (passwordLockEnabled) {
            PasswordChangeItem(onClick = onPasswordChangeClick)
        }
        BiometricsItem(
            enabled = biometricsLockEnabled,
            onEnabledChange = onBiometricsLockEnabledChange
        )
    }
}

@Composable
private fun AppBar(onBack: () -> Unit) {
    GBDiaryAppBar {
        Box(Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "뒤로"
                )
            }
            Text(
                text = "화면 잠금",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun PasswordItem(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
) {
    SettingsItem(
        name = "비밀번호",
        icon = painterResource(R.drawable.lock)
    ) {
        GBDiarySwitch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
        )
    }
}

@Composable
private fun PasswordChangeItem(onClick: () -> Unit) {
    SettingsItem(
        name = "비밀번호 변경",
        icon = painterResource(R.drawable.reset),
        onClick = onClick
    )
}

@Composable
private fun BiometricsItem(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
) {
    SettingsItem(
        name = "생체 인증",
        icon = painterResource(R.drawable.face_id)
    ) {
        GBDiarySwitch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
        )
    }
}

@Preview(name = "Screen lock screen")
@Preview(name = "Screen lock screen (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScreenLockScreenPreview() {
    GBDiaryTheme {
        ScreenLockSettingScreen(
            passwordLockEnabled = false,
            onPasswordLockEnabledChange = {},
            onPasswordChangeClick = {},
            biometricsLockEnabled = false,
            onBiometricsLockEnabledChange = {},
            onBack = {}
        )
    }
}