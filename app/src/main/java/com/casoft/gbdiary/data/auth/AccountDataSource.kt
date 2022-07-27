package com.casoft.gbdiary.data.auth

import android.accounts.Account
import com.casoft.gbdiary.model.Result
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

interface AccountDataSource {

    val account: Flow<Account?>

    fun checkExistingSignedInUser()
}