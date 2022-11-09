package com.casoft.gbdiary.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.components.GBDiaryAppBar
import com.casoft.gbdiary.ui.lock.KeypadElement
import com.casoft.gbdiary.ui.lock.PasswordInput
import com.casoft.gbdiary.ui.lock.PasswordInputState
import com.casoft.gbdiary.ui.lock.rememberPasswordInputState
import kotlinx.coroutines.launch

@Composable
fun PasswordRegistrationScreen(
    viewModel: PasswordRegistrationViewModel,
    onBack: () -> Unit,
) {
    val passwordInputState = rememberPasswordInputState()

    val input by viewModel.currentInput.collectAsState()
    var message by remember { mutableStateOf("비밀번호를 입력해주세요") }

    LaunchedEffect(viewModel) {
        launch {
            viewModel.isConfirmStep.collect {
                message = if (it) {
                    "비밀번호를 한 번 더 입력해주세요"
                } else {
                    "비밀번호를 입력해주세요"
                }
            }
        }

        launch {
            viewModel.verificationFailed.collect {
                message = "비밀번호가 일치하지 않습니다"
                passwordInputState.shakePasswordDots()
            }
        }

        launch {
            viewModel.registrationCompleted.collect {
                onBack()
            }
        }
    }

    PasswordRegistrationScreen(
        passwordInputState = passwordInputState,
        input = input,
        message = message,
        onInput = viewModel::onInput,
        onBack = onBack
    )
}

@Composable
private fun PasswordRegistrationScreen(
    passwordInputState: PasswordInputState,
    input: String,
    message: String,
    onInput: (KeypadElement) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Appbar(onClose = onBack)
        PasswordInput(
            state = passwordInputState,
            password = input,
            message = message,
            onInput = onInput,
        )
    }
}

@Composable
private fun Appbar(onClose: () -> Unit) {
    GBDiaryAppBar {
        Box(Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = "닫기"
                )
            }
        }
    }
}