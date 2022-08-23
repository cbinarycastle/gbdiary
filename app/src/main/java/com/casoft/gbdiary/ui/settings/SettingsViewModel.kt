package com.casoft.gbdiary.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.DisableNotificationUseCase
import com.casoft.gbdiary.domain.EnableNotificationUseCase
import com.casoft.gbdiary.domain.GetNotificationTimeUseCase
import com.casoft.gbdiary.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getNotificationTimeUseCase: GetNotificationTimeUseCase,
    private val enableNotificationUseCase: EnableNotificationUseCase,
    private val disableNotificationUseCase: DisableNotificationUseCase,
) : ViewModel() {

    val notificationTime = getNotificationTimeUseCase(Unit)
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

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                enableNotificationUseCase(Unit)
            } else {
                disableNotificationUseCase(Unit)
            }
        }
    }
}