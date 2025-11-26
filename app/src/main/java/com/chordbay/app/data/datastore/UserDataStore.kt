package com.chordbay.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(context: Context) {
    val dataStore = context.userCredentials

    private val USERNAME_KEY = androidx.datastore.preferences.core.stringPreferencesKey("username")

    suspend fun saveUsername(username: String) {
        dataStore.edit {
            it[USERNAME_KEY] = username
        }
    }

    fun getUsername(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }
    }
}
