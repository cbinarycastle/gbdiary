package com.casoft.gbdiary.data.settings

import com.casoft.gbdiary.model.TextAlign
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

interface SettingsDataSource {

    fun getNotificationTime(): Flow<LocalTime?>

    suspend fun setNotificationTime(time: LocalTime?)

    fun getTextAlign(): Flow<TextAlign>

    suspend fun setTextAlign(textAlign: TextAlign)
}