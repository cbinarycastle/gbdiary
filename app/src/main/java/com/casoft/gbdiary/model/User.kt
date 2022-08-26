package com.casoft.gbdiary.model

import android.accounts.Account
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

data class User(private val googleSignInAccount: GoogleSignInAccount) {

    val account: Account?
        get() = googleSignInAccount.account

    val email: String?
        get() = googleSignInAccount.email
}