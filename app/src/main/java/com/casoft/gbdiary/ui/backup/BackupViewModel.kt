package com.casoft.gbdiary.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.data.backup.BackupDataNotFoundException
import com.casoft.gbdiary.domain.BackupDataUseCase
import com.casoft.gbdiary.domain.ObserveAccountUseCase
import com.casoft.gbdiary.domain.SyncDataUseCase
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupViewModel @Inject constructor(
    observeAccountUseCase: ObserveAccountUseCase,
    private val backupDataUseCase: BackupDataUseCase,
    private val syncDataUseCase: SyncDataUseCase,
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

    fun sync() {
        account.value?.let {
            viewModelScope.launch {
                val result = syncDataUseCase(it)
                if (result is Result.Error &&
                    result.exception is BackupDataNotFoundException
                ) {
                    _message.emit("백업 데이터가 존재하지 않습니다.")
                }
            }
        } ?: viewModelScope.launch {
            _message.emit("로그인 후 이용 가능합니다.")
        }
    }
}