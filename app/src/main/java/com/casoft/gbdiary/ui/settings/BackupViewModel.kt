package com.casoft.gbdiary.ui.settings

import android.accounts.Account
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.data.backup.BackupDataNotFoundException
import com.casoft.gbdiary.domain.*
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.model.data
import com.casoft.gbdiary.ui.model.Progress
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BackupViewModel @Inject constructor(
    observeUserUseCase: ObserveUserUseCase,
    private val backupDataUseCase: BackupDataUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val checkExistingSignedInUserUseCase: CheckExistingSignedInUserUseCase,
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val user = observeUserUseCase(Unit)
        .map { it.data }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    val email = user
        .map { it?.email }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val _shouldSignIn = MutableSharedFlow<Unit>()
    val shouldSignIn = _shouldSignIn.asSharedFlow()

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    private val backupEvent = MutableSharedFlow<Account>()

    val backupProgress = backupEvent
        .flatMapLatest { account -> backupDataUseCase(account) }
        .map { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data == BackupResult.ALREADY_COMPLETED) {
                        _message.emit("이미 모든 변경사항이 백업되었습니다")
                    } else {
                        _message.emit("백업이 완료되었습니다")
                    }
                    Progress.NotInProgress
                }
                is Result.Error -> {
                    _message.emit("백업 도중 오류가 발생했습니다")
                    Progress.NotInProgress
                }
                is Result.Loading -> Progress.InProgress(result.progress ?: 0f)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Progress.NotInProgress
        )

    private val syncEvent = MutableSharedFlow<Account>()

    val syncProgress = syncEvent
        .flatMapLatest { account -> syncDataUseCase(account) }
        .map { result ->
            when (result) {
                is Result.Success -> {
                    _message.emit("복원이 완료되었습니다")
                    Progress.NotInProgress
                }
                is Result.Error -> {
                    if (result.exception is BackupDataNotFoundException) {
                        _message.emit("백업 데이터가 존재하지 않습니다.")
                    } else {
                        _message.emit("복원 도중 오류가 발생했습니다")
                    }
                    Progress.NotInProgress
                }
                is Result.Loading -> Progress.InProgress(result.progress ?: 0f)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Progress.NotInProgress
        )

    fun backup() {
        viewModelScope.launch {
            val account = user.value?.account
            if (account == null) {
                _shouldSignIn.emit(Unit)
            } else {
                backupEvent.emit(account)
            }
        }
    }

    fun sync() {
        viewModelScope.launch {
            val account = user.value?.account
            if (account == null) {
                _shouldSignIn.emit(Unit)
            } else {
                syncEvent.emit(account)
            }
        }
    }

    fun onSignedIn(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            if (task.isSuccessful.not()) {
                _message.emit("로그인에 실패했습니다.")
                return@launch
            }

            _message.emit("${task.result.email}으로 로그인 되었습니다")

            checkExistingSignedInUserUseCase(Unit)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleSignInClient.signOut()

            _message.emit("Google 계정 로그아웃 되었습니다")

            checkExistingSignedInUserUseCase(Unit)
        }
    }
}