package io.github.cbinarycastle.diary.domain

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.github.cbinarycastle.diary.data.auth.AuthDataSource
import io.github.cbinarycastle.diary.di.IoDispatcher
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