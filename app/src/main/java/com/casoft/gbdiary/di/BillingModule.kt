package com.casoft.gbdiary.di

import android.content.Context
import com.casoft.gbdiary.data.billing.BillingDataSource
import com.casoft.gbdiary.data.billing.GoogleBillingDataSource
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
class BillingModule {

    @Singleton
    @Provides
    fun provideBillingDataSource(
        @ApplicationContext applicationContext: Context,
        @ApplicationScope applicationScope: CoroutineScope,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BillingDataSource = GoogleBillingDataSource(
        context = applicationContext,
        externalScope = applicationScope,
        ioDispatcher = ioDispatcher
    )
}