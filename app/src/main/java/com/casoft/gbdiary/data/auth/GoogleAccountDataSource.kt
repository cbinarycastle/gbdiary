package com.casoft.gbdiary.data.auth

import android.accounts.Account
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GoogleAccountDataSource(private val context: Context) : AccountDataSource {

    private val _account = MutableStateFlow<Account?>(null)
    override val account: Flow<Account?> = _account

    override fun checkExistingSignedInUser() {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context)
        _account.value = lastSignedInAccount?.account
    }
}