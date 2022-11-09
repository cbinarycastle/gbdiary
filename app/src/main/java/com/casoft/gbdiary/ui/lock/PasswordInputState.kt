package com.casoft.gbdiary.ui.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberPasswordInputState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) = remember { PasswordInputState(coroutineScope) }

@Immutable
class PasswordInputState(private val coroutineScope: CoroutineScope) {

    val passwordDotsAnimatable = Animatable(0f)

    fun shakePasswordDots() {
        coroutineScope.launch {
            passwordDotsAnimatable.animateTo(
                0f,
                keyframes {
                    durationMillis = 500
                    -16f at 50
                    16f at 100
                    -8f at 150
                    8f at 200
                    -4f at 300
                    4f at 400
                }
            )
        }
    }
}