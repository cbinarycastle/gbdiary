package com.casoft.gbdiary.util

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

fun Context.biometricAuthenticate(
    promptInfo: BiometricPrompt.PromptInfo,
): Flow<BiometricAuthenticationResult> {
    return (this.findActivity() as FragmentActivity).biometricAuthenticate(promptInfo)
}

fun FragmentActivity.biometricAuthenticate(
    promptInfo: BiometricPrompt.PromptInfo,
): Flow<BiometricAuthenticationResult> = callbackFlow {
    val biometricPrompt = BiometricPrompt(
        this@biometricAuthenticate,
        ContextCompat.getMainExecutor(applicationContext),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                when (errorCode) {
                    BiometricPrompt.ERROR_HW_UNAVAILABLE,
                    BiometricPrompt.ERROR_UNABLE_TO_PROCESS,
                    BiometricPrompt.ERROR_NO_SPACE,
                    BiometricPrompt.ERROR_NO_BIOMETRICS,
                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL,
                    -> {
                        trySend(BiometricAuthenticationResult.UNRECOVERABLE_ERROR)
                    }
                    BiometricPrompt.ERROR_USER_CANCELED -> {
                        trySend(BiometricAuthenticationResult.CANCELED)
                    }
                    else -> {}
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                trySend(BiometricAuthenticationResult.SUCCESS)
            }

            override fun onAuthenticationFailed() {
                Timber.d("onAuthenticationFailed")
            }
        }
    )
    biometricPrompt.authenticate(promptInfo)

    awaitClose()
}

enum class BiometricAuthenticationResult {
    SUCCESS,
    CANCELED,
    UNRECOVERABLE_ERROR
}