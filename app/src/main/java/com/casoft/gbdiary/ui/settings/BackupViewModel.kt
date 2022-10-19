package com.casoft.gbdiary.ui.settings

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
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val LatestBackupDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd a h:mm")

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BackupViewModel @Inject constructor(
    observeUserUseCase: ObserveUserUseCase,
    isPremiumUserUseCase: IsPremiumUserUseCase,
    private val backupDataUseCase: BackupDataUseCase,
    private val syncDataUseCase: SyncDataUseCase,
    private val checkExistingSignedInUserUseCase: CheckExistingSignedInUserUseCase,
    observeLatestBackupDate: ObserveLatestBackupDate,
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

    private val isPremiumUser = isPremiumUserUseCase(Unit)
        .map { it.data ?: false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    val latestBackupDate = observeLatestBackupDate(Unit)
        .map { it.data?.format(LatestBackupDateTimeFormatter) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    private val _showRewardedAdEvent = MutableSharedFlow<BackupAction>()
    val showRewardedAdEvent = _showRewardedAdEvent

    private val _authError = MutableSharedFlow<BackupAuthError>()
    val authError = _authError.asSharedFlow()

    private val backupSignal = MutableSharedFlow<Unit>()

    val backupProgress = backupSignal
        .flatMapLatest {
            val account = user.value?.account
            if (account == null) {
                _message.emit("Google Drive 연동 후 이용 가능합니다.")
                emptyFlow()
            } else {
                backupDataUseCase(account)
            }
        }
        .map { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data == BackupResult.NO_DATA) {
                        _message.emit("백업할 데이터가 없습니다")
                    } else {
                        _message.emit("백업이 완료되었습니다")
                    }
                    Progress.NotInProgress
                }
                is Result.Error -> {
                    if (result.exception is UserRecoverableAuthIOException) {
                        _authError.emit(
                            BackupAuthError(
                                exception = result.exception,
                                action = BackupAction.BACKUP
                            )
                        )
                    } else {
                        _message.emit("백업 도중 오류가 발생했습니다")
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

    private val syncSignal = MutableSharedFlow<Unit>()

    val syncProgress = syncSignal
        .flatMapLatest {
            val account = user.value?.account
            if (account == null) {
                _message.emit("Google Drive 연동 후 이용 가능합니다.")
                emptyFlow()
            } else {
                syncDataUseCase(account)
            }
        }
        .map { result ->
            when (result) {
                is Result.Success -> {
                    _message.emit("복원이 완료되었습니다")
                    Progress.NotInProgress
                }
                is Result.Error -> {
                    when (result.exception) {
                        is BackupDataNotFoundException -> {
                            _message.emit("백업 데이터가 존재하지 않습니다.")
                        }
                        is UserRecoverableAuthIOException -> {
                            _authError.emit(
                                BackupAuthError(
                                    exception = result.exception,
                                    action = BackupAction.SYNC
                                )
                            )
                        }
                        else -> {
                            _message.emit("복원 도중 오류가 발생했습니다")
                        }
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

    private val _message = MutableSharedFlow<String>()
    val message = _message.asSharedFlow()

    fun tryBackup() {
        viewModelScope.launch {
            if (isPremiumUser.value) {
                backup()
            } else {
                _showRewardedAdEvent.emit(BackupAction.BACKUP)
            }
        }
    }

    fun backup() {
        viewModelScope.launch {
            backupSignal.emit(Unit)
        }
    }

    fun trySync() {
        viewModelScope.launch {
            if (isPremiumUser.value) {
                sync()
            } else {
                _showRewardedAdEvent.emit(BackupAction.SYNC)
            }
        }
    }

    fun sync() {
        viewModelScope.launch {
            syncSignal.emit(Unit)
        }
    }

    fun onSignedIn(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            if (task.isSuccessful.not()) {
                _message.emit("Google Drive 연동에 실패했습니다.")
                return@launch
            }

            _message.emit("${task.result.email}으로 연동 되었습니다")

            checkExistingSignedInUserUseCase(Unit)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            googleSignInClient.signOut()

            _message.emit("Google Drive 연동이 해제되었습니다")

            checkExistingSignedInUserUseCase(Unit)
        }
    }
}