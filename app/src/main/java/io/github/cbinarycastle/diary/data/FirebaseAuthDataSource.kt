package io.github.cbinarycastle.diary.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.github.cbinarycastle.diary.model.Result
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthDataSource(private val auth: FirebaseAuth) : AuthDataSource {

    override suspend fun signInWithGoogle(task: Task<GoogleSignInAccount>): Result<Unit> {
        return try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { firebaseAuthWithGoogle(it) }
                ?: Result.Error(RuntimeException("ID token is empty"))
        } catch (e: ApiException) {
            Timber.e(e, "Google sign in failed")
            Result.Error(RuntimeException("Google sign in failed"))
        }
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
                                task.exception ?: RuntimeException("Firebase auth with google failed")
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