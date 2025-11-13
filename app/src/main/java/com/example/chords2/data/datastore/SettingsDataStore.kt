package com.example.chords2.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.chords2.data.model.util.ColorMode
import com.example.chords2.data.model.util.Settings
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.data.model.util.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataStore(context: Context) {
    private val dataStore = context.dataStoreSettings
    @Suppress("UNCHECKED_CAST")
    fun <T> getSetting(setting: Settings<T>): Flow<T> {
        return dataStore.data.map { preferences ->
            val rawValue = preferences[setting.preferencesKey]
            when (setting) {
                is Settings.SortBySetting -> SortBy.valueOf(rawValue ?: setting.defaultValue.name)
                is Settings.ThemeSetting -> ThemeMode.valueOf(rawValue ?: setting.defaultValue.name)
                is Settings.FontSize -> (rawValue ?: setting.defaultValue.toString()).toInt()
                is Settings.ColorModeSetting -> ColorMode.valueOf(rawValue ?: setting.defaultValue.name)
            } as T
        }
    }

    suspend fun <T> setSetting(setting: Settings<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[setting.preferencesKey] = value.toString()
        }
    }

}