package com.casoft.gbdiary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.EnablePasswordLockUseCase
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.ui.lock.KeypadElement
import com.casoft.gbdiary.ui.lock.MAX_PASSWORD_LENGTH
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PasswordRegistrationViewModel @Inject constructor(
    private val enablePasswordLockUseCase: EnablePasswordLockUseCase,
) : ViewModel() {

    private val password = MutableStateFlow("")

    private val confirmPassword = MutableStateFlow("")

    private val _isConfirmStep = MutableStateFlow(false)
    val isConfirmStep = _isConfirmStep.asStateFlow()

    val currentInput = _isConfirmStep
        .flatMapLatest { if (it) confirmPassword else password }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ""
        )

    private val _verificationFailed = MutableSharedFlow<Unit>()
    val verificationFailed = _verificationFailed.asSharedFlow()

    private val _registrationCompleted = MutableSharedFlow<Unit>()
    val registrationCompleted = _registrationCompleted.asSharedFlow()

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    fun onInput(element: KeypadElement) {
        when (element) {
            is KeypadElement.Number -> {
                val input = element.value
                if (_isConfirmStep.value) {
                    inputConfirmPassword(input)
                } else {
                    inputPassword(input)
                }
            }
            KeypadElement.Delete -> {
                if (_isConfirmStep.value) {
                    removeLastConfirmPasswordInput()
                } else {
                    removeLastPasswordInput()
                }
            }
            KeypadElement.Empty -> {}
        }
    }

    private fun inputPassword(value: Int) {
        val newPassword = password.value + value.toString()
        if (newPassword.length > MAX_PASSWORD_LENGTH) {
            return
        }

        password.value = newPassword
        if (newPassword.length == MAX_PASSWORD_LENGTH) {
            _isConfirmStep.value = true
        }
    }

    private fun inputConfirmPassword(input: Int) {
        val newPassword = confirmPassword.value + input.toString()
        if (newPassword.length > MAX_PASSWORD_LENGTH) {
            return
        }

        confirmPassword.value = newPassword
        if (newPassword.length == MAX_PASSWORD_LENGTH) {
            verifyConfirmPassword()
        }
    }

    private fun verifyConfirmPassword() {
        viewModelScope.launch {
            val password = password.value

            if (password == confirmPassword.value) {
                when (enablePasswordLockUseCase(password)) {
                    is Result.Success -> {
                        _message.emit(Message.Toast("비밀번호가 설정되었습니다."))
                        _registrationCompleted.emit(Unit)
                    }
                    is Result.Error -> {
                        _message.emit(Message.Toast("비밀번호를 설정하지 못했습니다."))
                    }
                    is Result.Loading -> {}
                }
            } else {
                _verificationFailed.emit(Unit)
                confirmPassword.value = ""
            }
        }
    }

    private fun removeLastPasswordInput() {
        val value = password.value
        if (value.isNotEmpty()) {
            password.value = value.dropLast(1)
        }
    }

    private fun removeLastConfirmPasswordInput() {
        val value = confirmPassword.value
        if (value.isNotEmpty()) {
            confirmPassword.value = value.dropLast(1)
        }
    }
}