package com.chordbay.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
class FirstLaunchDatastore(context: Context) {
   val dataStore = context.firstLaunchDataStore

    fun isFirstLaunch(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[FIRST_LAUNCH_KEY] ?: true
        }
    }
    suspend fun setFirstLaunch() {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = false
        }
    }
}