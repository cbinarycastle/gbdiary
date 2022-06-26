package io.github.cbinarycastle.diary.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

    private val _signInNavigationAction = MutableSharedFlow<Unit>()
    val signInNavigationAction: SharedFlow<Unit> = _signInNavigationAction

    fun onSignIn() {
        viewModelScope.launch { _signInNavigationAction.emit(Unit) }
    }
}