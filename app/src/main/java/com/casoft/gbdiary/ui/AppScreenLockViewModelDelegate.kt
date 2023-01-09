package com.casoft.gbdiary.ui

import com.casoft.gbdiary.di.ApplicationScope
import com.casoft.gbdiary.domain.ObserveBiometricsSettingUseCase
import com.casoft.gbdiary.domain.ObservePasswordUseCase
import com.casoft.gbdiary.model.data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

private val InactivityTimeout = Duration.ofMinutes(1)

interface ScreenLockViewModelDelegate {

    val shouldLockScreen: StateFlow<Boolean?>

    fun unlockScreen()

    fun checkInactivityTimeout()

    fun turnToInactivity()
}

class ScreenLockViewModelDelegateImpl(
    observePasswordUseCase: ObservePasswordUseCase,
    observeBiometricsSettingUseCase: ObserveBiometricsSettingUseCase,
    @ApplicationScope applicationScope: CoroutineScope,
) : ScreenLockViewModelDelegate {

    private val passwordLockEnabled = observePasswordUseCase(Unit)
        .map { it.data != null }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val biometricsLockEnabled = observeBiometricsSettingUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val screenLockEnabled = combine(
        passwordLockEnabled,
        biometricsLockEnabled
    ) { passwordLockEnabled, biometricsLockEnabled ->
        if (passwordLockEnabled == null || biometricsLockEnabled == null) {
            null
        } else {
            passwordLockEnabled || biometricsLockEnabled
        }
    }.stateIn(
        scope = applicationScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private val inactivityTimeoutExceeded = MutableStateFlow(false)

    override val shouldLockScreen = combine(
        screenLockEnabled,
        inactivityTimeoutExceeded
    ) { screenLockEnabled, inactivityTimeoutExceeded ->
        if (screenLockEnabled == null) {
            null
        } else {
            screenLockEnabled && inactivityTimeoutExceeded
        }
    }.stateIn(
        scope = applicationScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private var inactivatedAt: LocalDateTime? = null

    init {
        applicationScope.launch {
            showScreenLockInitiallyIfEnabled()
        }
    }

    private suspend fun showScreenLockInitiallyIfEnabled() {
        screenLockEnabled
            .filterNotNull()
            .take(1)
            .collect { enabled ->
                if (enabled) {
                    inactivityTimeoutExceeded.value = true
                }
            }
    }

    override fun unlockScreen() {
        inactivityTimeoutExceeded.value = false
    }

    override fun checkInactivityTimeout() {
        if (inactivityTimeoutExceeded.value) {
            return
        }

        inactivatedAt?.let {
            val durationSinceInactivated = Duration.between(it, LocalDateTime.now())
            if (durationSinceInactivated >= InactivityTimeout) {
                inactivityTimeoutExceeded.value = true
            }
        }
    }

    override fun turnToInactivity() {
        inactivatedAt = LocalDateTime.now()
    }
}