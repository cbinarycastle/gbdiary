package com.casoft.gbdiary.data.auth

import com.casoft.gbdiary.model.Result
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface AuthDataSource {

    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<Unit>
}