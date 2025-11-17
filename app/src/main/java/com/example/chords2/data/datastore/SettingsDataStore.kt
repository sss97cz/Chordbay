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
    fun <T> getSetting(setting: Settings<T>): Flow<T> {
        return dataStore.data.map { preferences ->
            setting.parse(raw = preferences[setting.preferencesKey])
        }
    }

    suspend fun <T> setSetting(setting: Settings<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[setting.preferencesKey] = value.toString()
        }
    }
}