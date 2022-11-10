package com.casoft.gbdiary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.*
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.model.Message
import com.casoft.gbdiary.util.AuthenticationStatus
import com.casoft.gbdiary.util.BiometricsLockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreenLockViewModel @Inject constructor(
    observePasswordUseCase: ObservePasswordUseCase,
    observeBiometricsSettingUseCase: ObserveBiometricsSettingUseCase,
    private val disablePasswordLockUseCase: DisablePasswordLockUseCase,
    private val enableBiometricsLockUseCase: EnableBiometricsLockUseCase,
    private val disableBiometricsLockUseCase: DisableBiometricsLockUseCase,
    private val biometricsLockManager: BiometricsLockManager,
) : ViewModel() {

    val biometricEnrollIntent = biometricsLockManager.biometricEnrollIntent

    val passwordLockEnabled = observePasswordUseCase(Unit)
        .map { it.data != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val biometricsLockEnabled = observeBiometricsSettingUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    private val _shouldEnrollBiometric = MutableSharedFlow<Unit>()
    val shouldEnrollBiometric = _shouldEnrollBiometric.asSharedFlow()

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    fun disablePasswordLock() {
        viewModelScope.launch {
            val result = disablePasswordLockUseCase(Unit)
            if (result is Result.Error) {
                _message.emit(
                    Message.Toast("비밀번호 잠금 설정을 해제하지 못했습니다.")
                )
            }
        }
    }

    fun checkBiometricsLockAvailable() {
        viewModelScope.launch {
            when (biometricsLockManager.isBiometricsAvailable()) {
                AuthenticationStatus.SUCCESS, AuthenticationStatus.UNKNOWN -> {
                    enableBiometricsLock()
                }
                AuthenticationStatus.NONE_ENROLLED -> {
                    _shouldEnrollBiometric.emit(Unit)
                }
                else -> {
                    _message.emit(
                        Message.AlertDialog(
                            text = "해당 기기에서는 화면 잠금 기능을 지원하지 않습니다.",
                            confirmText = "확인"
                        )
                    )
                }
            }
        }
    }

    fun enableBiometricsLock() {
        viewModelScope.launch {
            val result = enableBiometricsLockUseCase(Unit)
            if (result is Result.Error) {
                _message.emit(
                    Message.Toast("생체 인증 잠금을 설정하지 못했습니다.")
                )
            }
        }
    }

    fun disableBiometricsLock() {
        viewModelScope.launch {
            val result = disableBiometricsLockUseCase(Unit)
            if (result is Result.Error) {
                _message.emit(
                    Message.Toast("생체 인증 잠금 설정을 해제하지 못했습니다.")
                )
            }
        }
    }
}