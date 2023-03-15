package com.casoft.gbdiary.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Qualifier

@InstallIn(SingletonComponent::class)
@Module
object FileModule {

    @ImageFilesDir
    @Provides
    fun provideImageFilesDir(
        @ApplicationContext context: Context,
    ): File = File(context.filesDir, "images")
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ImageFilesDir