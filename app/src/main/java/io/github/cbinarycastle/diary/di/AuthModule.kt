package io.github.cbinarycastle.diary.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.cbinarycastle.diary.data.AuthDataSource
import io.github.cbinarycastle.diary.data.FirebaseAuthDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun bindAuthDataSource(firebaseAuth: FirebaseAuth): AuthDataSource =
        FirebaseAuthDataSource(firebaseAuth)
}