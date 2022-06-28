package io.github.cbinarycastle.diary.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.cbinarycastle.diary.R
import io.github.cbinarycastle.diary.data.AuthDataSource
import io.github.cbinarycastle.diary.model.Result
import io.github.cbinarycastle.diary.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val authDataSource: AuthDataSource,
) : ViewModel() {

    private val _signInNavigationAction = MutableSharedFlow<Unit>()
    val signInNavigationAction = _signInNavigationAction.asSharedFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage = _errorMessage.asSharedFlow()

    fun onSignInClick() {
        viewModelScope.launch { _signInNavigationAction.emit(Unit) }
    }

    fun signInWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            when (val result = authDataSource.signInWithGoogle(task)) {
                is Result.Success -> _user.value = result.data
                is Result.Error -> _errorMessage.emit(R.string.sign_in_failed)
                else -> {}
            }
        }
    }

    fun checkSignedIn() {
        _user.value = firebaseAuth.currentUser?.email?.let { User(email = it) }
    }
}