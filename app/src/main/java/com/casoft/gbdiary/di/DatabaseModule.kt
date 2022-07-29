package com.casoft.gbdiary.di

import android.content.Context
import androidx.room.Room
import com.casoft.gbdiary.data.DATABASE_NAME
import com.casoft.gbdiary.data.GBDiaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext applicationContext: Context,
    ): GBDiaryDatabase {
        return Room.databaseBuilder(
            applicationContext,
            GBDiaryDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideDiaryItemDao(database: GBDiaryDatabase) = database.diaryItemDao()
}