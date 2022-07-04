package io.github.cbinarycastle.diary.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.cbinarycastle.diary.data.auth.AuthDataSource
import io.github.cbinarycastle.diary.data.auth.FirebaseAuthDataSource
import io.github.cbinarycastle.diary.data.auth.FirebaseUserDataSource
import io.github.cbinarycastle.diary.data.auth.UserDataSource
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideAuthDataSource(firebaseAuth: FirebaseAuth): AuthDataSource =
        FirebaseAuthDataSource(firebaseAuth)

    @Singleton
    @Provides
    fun provideUserDataSource(
        firebaseAuth: FirebaseAuth,
        @ApplicationScope applicationScope: CoroutineScope,
    ): UserDataSource {
        return FirebaseUserDataSource(
            firebaseAuth = firebaseAuth,
            externalScope = applicationScope
        )
    }
}