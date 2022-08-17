package com.casoft.gbdiary.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.casoft.gbdiary.model.TextAlign
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsDataSource(
    private val preferencesDataStore: DataStore<Preferences>,
) : SettingsDataSource {

    override fun getTextAlign(): Flow<TextAlign> {
        return preferencesDataStore.data.map { prefs ->
            prefs[PreferencesKeys.TEXT_ALIGN]?.let { value ->
                TextAlign.valueOf(value)
            } ?: TextAlign.LEFT
        }
    }

    override suspend fun setTextAlign(textAlign: TextAlign) {
        preferencesDataStore.edit {
            it[PreferencesKeys.TEXT_ALIGN] = textAlign.name
        }
    }

    object PreferencesKeys {
        val TEXT_ALIGN = stringPreferencesKey("text_align")
    }

    companion object {
        const val PREFS_NAME = "gbdiary_preferences"
    }
}