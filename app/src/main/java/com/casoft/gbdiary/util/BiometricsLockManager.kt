package com.casoft.gbdiary.util

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import javax.inject.Inject

private val Authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
} else {
    BIOMETRIC_WEAK or DEVICE_CREDENTIAL
}

class BiometricsLockManager @Inject constructor(private val biometricManager: BiometricManager) {

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("잠금 해제")
        .setDescription("화면 잠금 해제를 위해 본인 인증을 진행해주세요")
        .setAllowedAuthenticators(Authenticators)
        .build()

    val biometricEnrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
        putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, Authenticators)
    }

    fun isBiometricsAvailable(): AuthenticationStatus {
        return when (biometricManager.canAuthenticate(Authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> AuthenticationStatus.SUCCESS
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> AuthenticationStatus.UNKNOWN
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> AuthenticationStatus.UNSUPPORTED
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> AuthenticationStatus.HW_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> AuthenticationStatus.NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> AuthenticationStatus.NO_HARDWARE
            else -> AuthenticationStatus.SECURITY_UPDATE_REQUIRED
        }
    }
}

enum class AuthenticationStatus {
    SUCCESS,
    UNKNOWN,
    UNSUPPORTED,
    HW_UNAVAILABLE,
    NONE_ENROLLED,
    NO_HARDWARE,
    SECURITY_UPDATE_REQUIRED
}