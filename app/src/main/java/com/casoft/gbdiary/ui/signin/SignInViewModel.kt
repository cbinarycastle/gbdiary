package com.casoft.gbdiary.ui.signin

import android.accounts.Account
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.R
import com.casoft.gbdiary.domain.CheckExistingSignedInUserUseCase
import com.casoft.gbdiary.domain.ObserveAccountUseCase
import com.casoft.gbdiary.model.Result
import com.casoft.gbdiary.util.WhileViewSubscribed
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    observeUserAuthStateUseCase: ObserveAccountUseCase,
    private val checkExistingSignedInUserUseCase: CheckExistingSignedInUserUseCase,
    val googleSignInClient: GoogleSignInClient,
) : ViewModel() {

    private val _account: Flow<Result<Account?>> = observeUserAuthStateUseCase(Unit)
    val account = _account
        .map { (it as? Result.Success)?.data }
        .stateIn(viewModelScope, WhileViewSubscribed, null)

    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _signInNavigationAction = MutableSharedFlow<Unit>()
    val signInNavigationAction = _signInNavigationAction.asSharedFlow()

    fun onSignInClick() {
        viewModelScope.launch { _signInNavigationAction.emit(Unit) }
    }

    fun signInWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            if (task.isSuccessful.not()) {
                Timber.e("Sign in failed.")
                _errorMessage.emit(R.string.sign_in_failed)
                return@launch
            }

            checkExistingSignedInUserUseCase(Unit)
        }
    }
}