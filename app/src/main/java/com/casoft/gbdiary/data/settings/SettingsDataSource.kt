package com.casoft.gbdiary.data.settings

import com.casoft.gbdiary.model.TextAlign
import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {

    fun getTextAlign(): Flow<TextAlign>

    suspend fun setTextAlign(textAlign: TextAlign)
}