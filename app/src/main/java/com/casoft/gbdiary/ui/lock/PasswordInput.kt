package com.casoft.gbdiary.ui.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.casoft.gbdiary.R
import com.casoft.gbdiary.ui.theme.GBDiaryTheme

private val KeypadElements = arrayOf(
    arrayOf(KeypadElement.Number(1), KeypadElement.Number(2), KeypadElement.Number(3)),
    arrayOf(KeypadElement.Number(4), KeypadElement.Number(5), KeypadElement.Number(6)),
    arrayOf(KeypadElement.Number(7), KeypadElement.Number(8), KeypadElement.Number(9)),
    arrayOf(KeypadElement.Empty, KeypadElement.Number(0), KeypadElement.Delete),
)

@Composable
fun PasswordInput(
    password: String,
    message: String,
    onInput: (KeypadElement) -> Unit,
    state: PasswordInputState = rememberPasswordInputState(),
) {
    Column(Modifier.padding(bottom = 40.dp)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = message,
                style = GBDiaryTheme.typography.h6
            )
            Spacer(Modifier.height(32.dp))
            PasswordDots(
                password = password,
                offsetX = state.passwordDotsAnimatable.value
            )
        }
        Keypad(onInput = onInput)
    }
}

@Composable
private fun PasswordDots(password: String, offsetX: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.offset(x = offsetX.dp)
    ) {
        repeat(MAX_PASSWORD_LENGTH) { index ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(
                        if (index < password.length) {
                            GBDiaryTheme.gbDiaryColors.enabled
                        } else {
                            GBDiaryTheme.gbDiaryColors.border
                        }
                    )
            )
        }
    }
}

@Composable
private fun Keypad(
    onInput: (KeypadElement) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        KeypadElements.forEach { values ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                values.forEach { value ->
                    KeypadButton(
                        element = value,
                        onClick = onInput,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    element: KeypadElement,
    onClick: (KeypadElement) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .then(
                if (element == KeypadElement.Empty) {
                    Modifier
                } else {
                    Modifier.clickable { onClick(element) }
                }
            )
    ) {
        when (element) {
            is KeypadElement.Number -> {
                Text(
                    text = element.value.toString(),
                    style = GBDiaryTheme.typography.h4
                )
            }
            KeypadElement.Delete -> {
                Icon(
                    painter = painterResource(R.drawable.delete_num),
                    contentDescription = "지우기"
                )
            }
            KeypadElement.Empty -> {}
        }
    }
}

sealed interface KeypadElement {
    data class Number(val value: Int) : KeypadElement
    object Delete : KeypadElement
    object Empty : KeypadElement
}