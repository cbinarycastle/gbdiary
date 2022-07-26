package com.casoft.gbdiary.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.BackupDataUseCase
import com.casoft.gbdiary.domain.ObserveAccountUseCase
import com.casoft.gbdiary.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    observeAccountUseCase: ObserveAccountUseCase,
    private val backupDataUseCase: BackupDataUseCase,
) : ViewModel() {

    private val account = observeAccountUseCase(Unit)
        .map { it.data }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun backup() {
        account.value?.let {
            viewModelScope.launch {
                backupDataUseCase(it)
            }
        } ?: viewModelScope.launch {
            _message.emit("로그인 후 이용 가능합니다.")
        }
    }
}