package com.casoft.gbdiary.data.settings

import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.model.TextAlign
import com.casoft.gbdiary.model.Theme
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface SettingsDataSource {

    fun getNotificationTime(): Flow<LocalTime?>

    suspend fun setNotificationTime(time: LocalTime?)

    fun getPassword(): Flow<String?>

    suspend fun setPassword(password: String?)

    fun isBiometricsEnabled(): Flow<Boolean>

    suspend fun setBiometricsEnabled(enabled: Boolean)

    fun getTheme(): Flow<Theme>

    suspend fun setTheme(theme: Theme)

    fun getDiaryFontSize(): Flow<DiaryFontSize>

    suspend fun setDiaryFontSize(diaryFontSize: DiaryFontSize)

    fun getTextAlign(): Flow<TextAlign>

    suspend fun setTextAlign(textAlign: TextAlign)
}