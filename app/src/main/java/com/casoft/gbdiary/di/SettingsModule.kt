package com.casoft.gbdiary.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.casoft.gbdiary.data.settings.DataStoreSettingsDataSource
import com.casoft.gbdiary.data.settings.SettingsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SettingsModule {

    @Singleton
    @Provides
    fun provideSettingsDataSource(
        preferencesDataStore: DataStore<Preferences>,
    ): SettingsDataSource = DataStoreSettingsDataSource(preferencesDataStore)
}