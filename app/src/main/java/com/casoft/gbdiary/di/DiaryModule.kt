package com.casoft.gbdiary.di

import android.content.Context
import com.casoft.gbdiary.data.diary.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DiaryModule {

    @Singleton
    @Provides
    fun provideDiaryDataSource(diaryItemDao: DiaryItemDao): DiaryDataSource =
        DefaultDiaryDataSource(diaryItemDao)

    @Singleton
    @Provides
    fun provideDiaryImageDataSource(@ApplicationContext context: Context): DiaryImageDataSource =
        DefaultDiaryImageDataSource(context)
}