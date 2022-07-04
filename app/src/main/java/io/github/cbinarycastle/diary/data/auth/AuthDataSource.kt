package io.github.cbinarycastle.diary.data.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import io.github.cbinarycastle.diary.model.Result
import io.github.cbinarycastle.diary.model.User

interface AuthDataSource {

    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<Unit>
}