package com.casoft.gbdiary.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.casoft.gbdiary.data.diary.DiaryItemDao
import com.casoft.gbdiary.data.review.GoogleReviewDataSource
import com.casoft.gbdiary.data.review.ReviewDataSource
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ReviewModule {

    @Singleton
    @Provides
    fun provideReviewManager(
        @ApplicationContext context: Context,
    ): ReviewManager = ReviewManagerFactory.create(context)

    @Singleton
    @Provides
    fun provideReviewDataSource(
        preferencesDataStore: DataStore<Preferences>,
        diaryItemDao: DiaryItemDao,
        reviewManager: ReviewManager,
        @ApplicationScope applicationScope: CoroutineScope,
    ): ReviewDataSource = GoogleReviewDataSource(
        preferencesDataStore = preferencesDataStore,
        diaryItemDao = diaryItemDao,
        reviewManager = reviewManager,
        externalScope = applicationScope
    )
}