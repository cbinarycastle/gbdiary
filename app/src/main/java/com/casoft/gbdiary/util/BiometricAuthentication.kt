package com.casoft.gbdiary.util

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.biometricAuthenticate(promptInfo: BiometricPrompt.PromptInfo): Boolean {
    return (this.findActivity() as FragmentActivity).biometricAuthenticate(promptInfo)
}

suspend fun FragmentActivity.biometricAuthenticate(
    promptInfo: BiometricPrompt.PromptInfo,
): Boolean = suspendCoroutine { cont ->
    val biometricPrompt = BiometricPrompt(
        this,
        ContextCompat.getMainExecutor(applicationContext),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                cont.resume(false)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                cont.resume(true)
            }

            override fun onAuthenticationFailed() {
                cont.resume(false)
            }
        }
    )

    biometricPrompt.authenticate(promptInfo)
}