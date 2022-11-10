package com.casoft.gbdiary.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.DisableBiometricsLockUseCase
import com.casoft.gbdiary.domain.ObserveBiometricsSettingUseCase
import com.casoft.gbdiary.domain.ObservePasswordUseCase
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.util.BiometricsLockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreenLockViewModel @Inject constructor(
    observePasswordUseCase: ObservePasswordUseCase,
    observeBiometricsSettingUseCase: ObserveBiometricsSettingUseCase,
    private val disableBiometricsLockUseCase: DisableBiometricsLockUseCase,
    biometricsLockManager: BiometricsLockManager,
) : ViewModel() {

    val biometricsPromptInfo = biometricsLockManager.promptInfo

    private val password = observePasswordUseCase(Unit)
        .map { it.data }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val passwordLockEnabled = password
        .map { it != null }
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

    private val _passwordInput = MutableStateFlow("")
    val passwordInput = _passwordInput.asStateFlow()

    private val _verificationFailed = MutableSharedFlow<Unit>()
    val verificationFailed = _verificationFailed.asSharedFlow()

    private val _verificationCompleted = MutableSharedFlow<Unit>()
    val verificationCompleted = _verificationCompleted.asSharedFlow()

    fun onInput(element: KeypadElement) {
        when (element) {
            is KeypadElement.Number -> {
                inputPassword(element.value)
            }
            KeypadElement.Delete -> {
                removeLastInput()
            }
            KeypadElement.Empty -> {}
        }
    }

    private fun inputPassword(value: Int) {
        val newPassword = _passwordInput.value + value.toString()
        if (newPassword.length > MAX_PASSWORD_LENGTH) {
            return
        }

        _passwordInput.value = newPassword
        if (newPassword.length == MAX_PASSWORD_LENGTH) {
            verifyPassword()
        }
    }

    private fun verifyPassword() {
        viewModelScope.launch {
            if (passwordInput.value == password.value) {
                _verificationCompleted.emit(Unit)
            } else {
                _verificationFailed.emit(Unit)
                _passwordInput.value = ""
            }
        }
    }

    private fun removeLastInput() {
        val value = _passwordInput.value
        if (value.isNotEmpty()) {
            _passwordInput.value = value.dropLast(1)
        }
    }

    fun disableBiometricsLock() {
        viewModelScope.launch {
            disableBiometricsLockUseCase(Unit)
        }
    }
}