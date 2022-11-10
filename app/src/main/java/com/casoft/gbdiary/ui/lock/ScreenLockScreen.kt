package com.casoft.gbdiary.ui.lock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.util.BiometricAuthenticationResult
import com.casoft.gbdiary.util.biometricAuthenticate
import kotlinx.coroutines.launch

@Composable
fun ScreenLockScreen(
    viewModel: ScreenLockViewModel,
    onUnlock: () -> Unit,
    onClose: () -> Unit,
) {
    val context = LocalContext.current

    val passwordInputState = rememberPasswordInputState()

    val passwordLockEnabled by viewModel.passwordLockEnabled.collectAsState()
    val biometricsLockEnabled by viewModel.biometricsLockEnabled.collectAsState()

    val passwordInput by viewModel.passwordInput.collectAsState()
    var message by remember { mutableStateOf("비밀번호를 입력해주세요") }

    LaunchedEffect(biometricsLockEnabled) {
        if (biometricsLockEnabled) {
            context.biometricAuthenticate(viewModel.biometricsPromptInfo).collect { result ->
                when (result) {
                    BiometricAuthenticationResult.SUCCESS -> onUnlock()
                    BiometricAuthenticationResult.CANCELED -> {
                        if (!passwordLockEnabled) {
                            onClose()
                        }
                    }
                    BiometricAuthenticationResult.UNRECOVERABLE_ERROR -> {
                        // 해결 불가능한 에러일 경우 생체 인증 잠금을 해제하고 메인 화면으로 이동.
                        viewModel.disableBiometricsLock()
                        if (!passwordLockEnabled) {
                            onUnlock()
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel) {
        launch {
            viewModel.verificationFailed.collect {
                message = "비밀번호가 일치하지 않습니다"
                passwordInputState.shakePasswordDots()
            }
        }

        launch {
            viewModel.verificationCompleted.collect {
                onUnlock()
            }
        }
    }

    ScreenLockScreen(
        passwordLockEnabled = passwordLockEnabled,
        passwordInputState = passwordInputState,
        password = passwordInput,
        onInput = viewModel::onInput,
        message = message
    )
}

@Composable
private fun ScreenLockScreen(
    passwordLockEnabled: Boolean,
    passwordInputState: PasswordInputState,
    password: String,
    onInput: (KeypadElement) -> Unit,
    message: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp)
    ) {
        if (passwordLockEnabled) {
            PasswordInput(
                state = passwordInputState,
                password = password,
                onInput = onInput,
                message = message
            )
        }
    }
}