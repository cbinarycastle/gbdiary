package com.casoft.gbdiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.*
import com.casoft.gbdiary.model.Theme
import com.casoft.gbdiary.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val checkExistingSignedInUserUseCase: CheckExistingSignedInUserUseCase,
    private val queryPurchasesUseCase: QueryPurchasesUseCase,
    observeThemeUseCase: ObserveThemeUseCase,
    observePasswordUseCase: ObservePasswordUseCase,
    observeBiometricsSettingUseCase: ObserveBiometricsSettingUseCase,
    observeNotificationTimeUseCase: ObserveNotificationTimeUseCase,
    private val disableNotificationUseCase: DisableNotificationUseCase,
) : ViewModel() {

    val theme = observeThemeUseCase(Unit)
        .map { result -> result.data ?: Theme.SYSTEM }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Theme.SYSTEM
        )

    private val passwordLockEnabled = observePasswordUseCase(Unit)
        .take(1) // 앱 실행 이후의 변경사항은 받지 않음.
        .map { it.data != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val biometricsLockEnabled = observeBiometricsSettingUseCase(Unit)
        .take(1) // 앱 실행 이후의 변경사항은 받지 않음.
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val screenLockEnabled = combine(
        passwordLockEnabled,
        biometricsLockEnabled
    ) { passwordLockEnabled, biometricsLockEnabled ->
        if (passwordLockEnabled == null || biometricsLockEnabled == null) {
            null
        } else {
            passwordLockEnabled || biometricsLockEnabled
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    val notificationEnabled = observeNotificationTimeUseCase(Unit)
        .map { it.data != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    fun checkExistingSignedInUser() {
        viewModelScope.launch {
            checkExistingSignedInUserUseCase(Unit)
        }
    }

    fun queryPurchases() {
        viewModelScope.launch {
            queryPurchasesUseCase(Unit)
        }
    }

    fun disableNotification() {
        viewModelScope.launch {
            disableNotificationUseCase(Unit)
        }
    }
}