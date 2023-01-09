package com.casoft.gbdiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.*
import com.casoft.gbdiary.model.Theme
import com.casoft.gbdiary.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val checkExistingSignedInUserUseCase: CheckExistingSignedInUserUseCase,
    private val queryPurchasesUseCase: QueryPurchasesUseCase,
    observeThemeUseCase: ObserveThemeUseCase,
    observeNotificationTimeUseCase: ObserveNotificationTimeUseCase,
    private val disableNotificationUseCase: DisableNotificationUseCase,
    screenLockViewModelDelegate: ScreenLockViewModelDelegate,
) : ViewModel(), ScreenLockViewModelDelegate by screenLockViewModelDelegate {

    val theme = observeThemeUseCase(Unit)
        .map { result -> result.data ?: Theme.SYSTEM }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Theme.SYSTEM
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