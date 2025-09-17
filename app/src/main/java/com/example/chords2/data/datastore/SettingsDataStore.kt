package com.example.chords2.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.chords2.data.model.util.Settings
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.data.model.util.ThemeMode
import com.example.chords2.ui.composable.screen.SettingsScreen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class SettingsDataStore(context: Context) {
    private val dataStore = context.dataStore
    @Suppress("UNCHECKED_CAST")
    fun <T> getSetting(setting: Settings<T>): Flow<T> {
        return dataStore.data.map { preferences ->
            val rawValue = preferences[setting.preferencesKey]
            when (setting) {
                is Settings.SortBySetting -> SortBy.valueOf(rawValue ?: setting.defaultValue.name)
                is Settings.ThemeSetting -> ThemeMode.valueOf(rawValue ?: setting.defaultValue.name)
                is Settings.FontSize -> (rawValue ?: setting.defaultValue.toString()).toInt()
            } as T
        }
    }

    suspend fun <T> setSetting(setting: Settings<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[setting.preferencesKey] = value.toString()
        }
    }

}