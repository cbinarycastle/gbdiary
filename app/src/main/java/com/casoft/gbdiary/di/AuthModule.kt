package com.casoft.gbdiary.di

import android.content.Context
import com.casoft.gbdiary.data.auth.AccountDataSource
import com.casoft.gbdiary.data.auth.GoogleAccountDataSource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Singleton
    @Provides
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder()
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    @Singleton
    @Provides
    fun provideAccountDataSource(
        @ApplicationContext applicationContext: Context,
    ): AccountDataSource = GoogleAccountDataSource(applicationContext)
}