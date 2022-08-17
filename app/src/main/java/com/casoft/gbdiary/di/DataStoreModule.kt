package com.casoft.gbdiary.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.casoft.gbdiary.data.settings.DataStoreSettingsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context) = context.datastore
}

private val Context.datastore by preferencesDataStore(name = DataStoreSettingsDataSource.PREFS_NAME)