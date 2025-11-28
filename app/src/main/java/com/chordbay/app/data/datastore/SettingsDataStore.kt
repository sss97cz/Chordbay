package com.chordbay.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.chordbay.app.data.model.settings.Settings
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