package com.casoft.gbdiary.di

import com.casoft.gbdiary.domain.ObserveBiometricsSettingUseCase
import com.casoft.gbdiary.domain.ObservePasswordUseCase
import com.casoft.gbdiary.ui.ScreenLockViewModelDelegate
import com.casoft.gbdiary.ui.ScreenLockViewModelDelegateImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppScreenLockViewModelDelegateModule {

    @Singleton
    @Provides
    fun provideScreenLockViewModelDelegate(
        observePasswordUseCase: ObservePasswordUseCase,
        observeBiometricsSettingUseCase: ObserveBiometricsSettingUseCase,
        @ApplicationScope applicationScope: CoroutineScope,
    ): ScreenLockViewModelDelegate {
        return ScreenLockViewModelDelegateImpl(
            observePasswordUseCase = observePasswordUseCase,
            observeBiometricsSettingUseCase = observeBiometricsSettingUseCase,
            applicationScope = applicationScope
        )
    }
}