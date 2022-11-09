package com.casoft.gbdiary.di

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context) = context.datastore
}

object PreferencesKeys {
    val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
    val PASSWORD = stringPreferencesKey("password")
    val BIOMETRICS_ENABLED = booleanPreferencesKey("biometrics_enabled")
    val THEME = stringPreferencesKey("theme")
    val TEXT_ALIGN = stringPreferencesKey("text_align")
    val LATEST_LAUNCH_REVIEW_FLOW_DATE = longPreferencesKey("latest_launch_review_flow_date")
    val LATEST_BACKUP_DATE = longPreferencesKey("latest_backup_date")
}

private const val PREFS_NAME = "gbdiary_preferences"

private val Context.datastore by preferencesDataStore(name = PREFS_NAME)