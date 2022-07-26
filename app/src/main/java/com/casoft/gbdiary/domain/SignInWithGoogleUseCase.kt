package com.casoft.gbdiary.domain

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.casoft.gbdiary.data.auth.AuthDataSource
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authDataSource: AuthDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<GoogleSignInAccount, Unit>(ioDispatcher) {

    override suspend fun execute(params: GoogleSignInAccount) {
        authDataSource.signInWithGoogle(account = params)
    }
}