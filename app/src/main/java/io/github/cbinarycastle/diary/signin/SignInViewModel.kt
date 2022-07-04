package io.github.cbinarycastle.diary.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.cbinarycastle.diary.R
import io.github.cbinarycastle.diary.domain.ObserveUserAuthStateUseCase
import io.github.cbinarycastle.diary.domain.SignInWithGoogleUseCase
import io.github.cbinarycastle.diary.extensions.WhileViewSubscribed
import io.github.cbinarycastle.diary.model.Result
import io.github.cbinarycastle.diary.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    observeUserAuthStateUseCase: ObserveUserAuthStateUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val userAuthState: Flow<Result<User?>> = observeUserAuthStateUseCase(Unit)

    val user = userAuthState
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
            val account = try {
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                Timber.e(e, "Google sign in failed")
                _errorMessage.emit(R.string.sign_in_failed)
                return@launch
            }

            val result = signInWithGoogleUseCase(account)
            if (result is Result.Error) {
                _errorMessage.emit(R.string.sign_in_failed)
            }
        }
    }
}