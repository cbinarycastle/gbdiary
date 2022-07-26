package com.casoft.gbdiary.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.casoft.gbdiary.data.auth.AuthDataSource
import com.casoft.gbdiary.data.auth.FirebaseAuthDataSource
import com.casoft.gbdiary.data.auth.FirebaseUserDataSource
import com.casoft.gbdiary.data.auth.UserDataSource
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