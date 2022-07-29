package com.casoft.gbdiary.di

import com.casoft.gbdiary.data.diary.DefaultDiaryDataSource
import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.DiaryItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DiaryModule {

    @Singleton
    @Provides
    fun provideDiaryDataSource(diaryItemDao: DiaryItemDao): DiaryDataSource =
        DefaultDiaryDataSource(diaryItemDao)
}