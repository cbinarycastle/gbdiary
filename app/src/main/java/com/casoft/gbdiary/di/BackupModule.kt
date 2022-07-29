package com.casoft.gbdiary.di

import android.content.Context
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.GoogleDriveBackupDataSource
import com.casoft.gbdiary.data.diary.DiaryItemDao
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class BackupModule {

    @Singleton
    @Provides
    fun provideBackupDataSource(
        @ApplicationContext applicationContext: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        diaryItemDao: DiaryItemDao,
        gson: Gson,
    ): BackupDataSource = GoogleDriveBackupDataSource(
        context = applicationContext,
        ioDispatcher = ioDispatcher,
        diaryItemDao = diaryItemDao,
        gson = gson,
    )
}