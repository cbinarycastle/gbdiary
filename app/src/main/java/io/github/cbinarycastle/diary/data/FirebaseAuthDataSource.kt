package io.github.cbinarycastle.diary.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.cbinarycastle.diary.model.Result
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthDataSource(private val auth: FirebaseAuth) : AuthDataSource {

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<Unit> {
        return account.idToken?.let { firebaseAuthWithGoogle(it) }
            ?: Result.Error(RuntimeException("ID token is empty"))
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): Result<Unit> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return suspendCoroutine { continuation ->
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("signInWithCredential:success")
                        auth.currentUser?.email?.let { continuation.resume(Result.Success(Unit)) }
                            ?: continuation.resume(Result.Error(RuntimeException("Email is empty")))
                    } else {
                        Timber.d(task.exception, "signInWithCredential:failure")
                        continuation.resume(
                            Result.Error(
                                task.exception
                                    ?: RuntimeException("Firebase auth with google failed")
                            )
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "signInWithCredential:failure")
                    continuation.resume(Result.Error(exception))
                }
        }
    }
}