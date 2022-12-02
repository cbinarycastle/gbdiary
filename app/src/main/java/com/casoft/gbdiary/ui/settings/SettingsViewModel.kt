package com.casoft.gbdiary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.*
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    isPremiumUserUseCase: IsPremiumUserUseCase,
    private val enableNotificationUseCase: EnableNotificationUseCase,
    private val disableNotificationUseCase: DisableNotificationUseCase,
    observeNotificationTimeUseCase: ObserveNotificationTimeUseCase,
    private val setNotificationTimeUseCase: SetNotificationTimeUseCase,
) : ViewModel() {

    val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = true
        )

    val notificationTime = observeNotificationTimeUseCase(Unit)
        .map { result -> result.data }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    val notificationEnabled = notificationTime
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    private val _message = MutableSharedFlow<Message>()
    val message = _message.asSharedFlow()

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                enableNotificationUseCase(Unit)
            } else {
                disableNotificationUseCase(Unit)
            }
        }
    }

    fun setNotificationTime(time: LocalTime) {
        viewModelScope.launch {
            setNotificationTimeUseCase(time)
        }
    }
}