package com.casoft.gbdiary.data.user

import android.content.Context
import com.casoft.gbdiary.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GoogleUserDataSource(private val context: Context) : UserDataSource {

    private val _user = MutableStateFlow<User?>(null)
    override val user: Flow<User?> = _user

    override fun checkExistingSignedInUser() {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context)
        _user.value = lastSignedInAccount?.let { User(lastSignedInAccount) }
    }
}