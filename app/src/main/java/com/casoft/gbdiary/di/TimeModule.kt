package com.casoft.gbdiary.di

import com.casoft.gbdiary.time.DefaultTimeProvider
import com.casoft.gbdiary.time.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object TimeModule {

    @Singleton
    @Provides
    fun provideTimeProvider(): TimeProvider = DefaultTimeProvider
}