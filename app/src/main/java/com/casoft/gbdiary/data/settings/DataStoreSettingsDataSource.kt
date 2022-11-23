package com.casoft.gbdiary.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.casoft.gbdiary.di.PreferencesKeys
import com.casoft.gbdiary.model.DiaryFontSize
import com.casoft.gbdiary.model.TextAlign
import com.casoft.gbdiary.model.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val NotificationTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

class DataStoreSettingsDataSource(
    private val preferencesDataStore: DataStore<Preferences>,
) : SettingsDataSource {

    override fun getNotificationTime(): Flow<LocalTime?> {
        return preferencesDataStore.data.map { prefs ->
            prefs[PreferencesKeys.NOTIFICATION_TIME]?.let {
                LocalTime.parse(it, NotificationTimeFormatter)
            }
        }
    }

    override suspend fun setNotificationTime(time: LocalTime?) {
        preferencesDataStore.edit { prefs ->
            if (time == null) {
                prefs.remove(PreferencesKeys.NOTIFICATION_TIME)
            } else {
                prefs[PreferencesKeys.NOTIFICATION_TIME] = time.format(NotificationTimeFormatter)
            }
        }
    }

    override fun getPassword(): Flow<String?> {
        return preferencesDataStore.data.map { prefs ->
            prefs[PreferencesKeys.PASSWORD]
        }
    }

    override suspend fun setPassword(password: String?) {
        preferencesDataStore.edit { prefs ->
            if (password == null) {
                prefs.remove(PreferencesKeys.PASSWORD)
            } else {
                prefs[PreferencesKeys.PASSWORD] = password
            }
        }
    }

    override fun isBiometricsEnabled(): Flow<Boolean> {
        return preferencesDataStore.data.map { prefs ->
            prefs[PreferencesKeys.BIOMETRICS_ENABLED] ?: false
        }
    }

    override suspend fun setBiometricsEnabled(enabled: Boolean) {
        preferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.BIOMETRICS_ENABLED] = enabled
        }
    }

    override fun getTheme(): Flow<Theme> {
        return preferencesDataStore.data.map { prefs ->
            prefs[PreferencesKeys.THEME]?.let { Theme.valueOf(it) }
                ?: Theme.SYSTEM
        }
    }

    override suspend fun setTheme(theme: Theme) {
        preferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME] = theme.name
        }
    }

    override fun getDiaryFontSize(): Flow<DiaryFontSize> {
        return preferencesDataStore.data.map { prefs ->
            prefs[PreferencesKeys.DIARY_FONT_SIZE]?.let {
                DiaryFontSize.valueOf(it)
            } ?: DiaryFontSize.M
        }
    }

    override suspend fun setDiaryFontSize(diaryFontSize: DiaryFontSize) {
        preferencesDataStore.edit { prefs ->
            prefs[PreferencesKeys.DIARY_FONT_SIZE] = diaryFontSize.name
        }
    }

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
}