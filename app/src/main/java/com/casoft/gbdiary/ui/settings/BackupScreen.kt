package com.casoft.gbdiary.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ad.BackupRewardedAd
import com.casoft.gbdiary.ui.components.GBDiaryAlertDialog
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.components.Progress
import com.casoft.gbdiary.ui.model.Progress
import com.casoft.gbdiary.ui.theme.GBDiaryTheme
import com.casoft.gbdiary.util.findActivity
import com.casoft.gbdiary.util.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch

@Composable
fun BackupScreen(
    viewModel: BackupViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val signInClient = viewModel.googleSignInClient

    val coroutineScope = rememberCoroutineScope()
    val rewardedAd = remember {
        BackupRewardedAd(
            activity = context.findActivity(),
            coroutineScope = coroutineScope
        )
    }

    val email by viewModel.email.collectAsState()
    val backupProgress by viewModel.backupProgress.collectAsState()
    val syncProgress by viewModel.syncProgress.collectAsState()

    val googleSignInLauncher = rememberLauncherForActivityResult(StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        viewModel.onSignedIn(task)
    }

    LaunchedEffect(viewModel) {
        launch {
            viewModel.showRewardedAdEvent.collect { action ->
                rewardedAd.apply {
                    showAd {
                        when (action) {
                            RewardedAction.BACKUP -> viewModel.backup()
                            RewardedAction.SYNC -> viewModel.sync()
                        }
                    }
                }
            }
        }

        launch {
            viewModel.message.collect {
                context.toast(it)
            }
        }
    }

    BackupScreen(
        signedInEmail = email,
        backupProgress = backupProgress,
        syncProgress = syncProgress,
        backup = viewModel::tryBackup,
        sync = viewModel::trySync,
        signIn = { googleSignInLauncher.launch(signInClient.signInIntent) },
        signOut = viewModel::signOut,
        onBack = onBack
    )
}

@Composable
private fun BackupScreen(
    signedInEmail: String?,
    backupProgress: Progress,
    syncProgress: Progress,
    backup: () -> Unit,
    sync: () -> Unit,
    signIn: () -> Unit,
    signOut: () -> Unit,
    onBack: () -> Unit,
) {
    var showSignInDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Column {
                AppBar(onBack)
                Text(
                    text = "Google Drive 에 데이터를 백업하여 핸드폰이 바뀌어도 간편하게 복구할 수 있습니다. 주기적으로 백업해야 데이터를 안전하게 보관할 수 있습니다",
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .alpha(0.4f)
                )
                Spacer(Modifier.height(40.dp))
                SettingsItem(
                    name = "데이터 백업",
                    icon = painterResource(R.drawable.backup),
                    onClick = {
                        if (signedInEmail == null) {
                            showSignInDialog = true
                        } else {
                            showBackupDialog = true
                        }
                    }
                )
                SettingsItem(
                    name = "데이터 복원",
                    icon = painterResource(R.drawable.restore),
                    onClick = {
                        if (signedInEmail == null) {
                            showSignInDialog = true
                        } else {
                            showSyncDialog = true
                        }
                    }
                )
                Divider(Modifier.padding(horizontal = 24.dp, vertical = 16.dp))
                if (signedInEmail == null) {
                    SettingsItem(
                        name = "Google 계정 로그인",
                        icon = painterResource(R.drawable.logout),
                        onClick = signIn
                    )
                } else {
                    SettingsItem(
                        name = "로그아웃",
                        icon = painterResource(R.drawable.logout),
                        onClick = { showSignOutDialog = true }
                    ) {
                        Text(
                            text = signedInEmail,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            style = GBDiaryTheme.typography.subtitle1,
                            modifier = Modifier.alpha(0.4f)
                        )
                    }
                }
            }

            if (showBackupDialog) {
                GBDiaryAlertDialog(
                    onConfirm = {
                        showBackupDialog = false
                        backup()
                    },
                    onDismiss = { showBackupDialog = false },
                    content = {
                        Text(
                            text = "백업을 진행할까요?\n모든 변경사항이 동기화됩니다\n프리미엄 사용자가 아닌 경우 동영상 시청 후 진행됩니다",
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmText = { Text("백업") },
                    dismissText = { Text("취소") },
                )
            }

            if (showSyncDialog) {
                GBDiaryAlertDialog(
                    onConfirm = {
                        showSyncDialog = false
                        sync()
                    },
                    onDismiss = { showSyncDialog = false },
                    content = {
                        Text(
                            text = "복원을 진행할까요?\n기존 데이터에 덮어씌워집니다\n프리미엄 사용자가 아닌 경우 동영상 시청 후 진행됩니다",
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmText = { Text("복원") },
                    dismissText = { Text("취소") },
                )
            }

            if (showSignInDialog) {
                GBDiaryAlertDialog(
                    onConfirm = {
                        showSignInDialog = false
                        signIn()
                    },
                    onDismiss = { showSignInDialog = false },
                    content = {
                        Text(
                            text = "Google 로그인이 필요한 서비스입니다.\n로그인 하시겠습니까?",
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmText = { Text("로그인") },
                    dismissText = { Text("취소") },
                )
            }

            if (showSignOutDialog) {
                GBDiaryAlertDialog(
                    onConfirm = {
                        showSignOutDialog = false
                        signOut()
                    },
                    onDismiss = { showSignOutDialog = false },
                    content = { Text("로그아웃 하시겠습니까?") },
                    confirmText = { Text("로그아웃") },
                    dismissText = { Text("취소") },
                )
            }
        }

        if (backupProgress is Progress.InProgress) {
            Progress(progress = backupProgress.value)
        }

        if (syncProgress is Progress.InProgress) {
            Progress(progress = syncProgress.value)
        }
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
                text = Settings.BACKUP.text,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}