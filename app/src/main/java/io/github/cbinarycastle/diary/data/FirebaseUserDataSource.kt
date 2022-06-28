package io.github.cbinarycastle.diary.data

import com.google.firebase.auth.FirebaseAuth
import io.github.cbinarycastle.diary.di.ApplicationScope
import io.github.cbinarycastle.diary.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

class FirebaseUserDataSource(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationScope private val externalScope: CoroutineScope,
) : UserDataSource {

    private val user: Flow<User?> = callbackFlow {
        val authStateListener: (FirebaseAuth) -> Unit = { auth ->
            auth.currentUser?.email?.let { trySend(User(it)) }
                ?: trySend(null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }.shareIn(
        scope = externalScope,
        started = SharingStarted.WhileSubscribed(),
        replay = 1
    )

    override fun getUser(): Flow<User?> = user
}