package com.casoft.gbdiary.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.data.backup.GoogleDriveBackupDataSource
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class BackupModule {

    @Singleton
    @Provides
    fun provideBackupDataSource(
        @ApplicationContext applicationContext: Context,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        preferencesDataStore: DataStore<Preferences>,
        gson: Gson,
    ): BackupDataSource = GoogleDriveBackupDataSource(
        context = applicationContext,
        externalScope = applicationScope,
        ioDispatcher = ioDispatcher,
        preferencesDataStore = preferencesDataStore,
        gson = gson,
    )
}