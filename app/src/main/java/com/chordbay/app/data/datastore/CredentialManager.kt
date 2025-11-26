package com.chordbay.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.chordbay.app.data.model.TokenPair
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "auth_credentials")

class CredentialManager(private val context: Context) {

    private val encryptor: Encryptor by lazy { Encryptor() }

    private var refreshToken = DataStoreEntity(stringPreferencesKey(REFRESH_TOKEN))
    private var accessToken = DataStoreEntity(stringPreferencesKey(ACCESS_TOKEN))

    inner class DataStoreEntity(private val key: Preferences.Key<String>) {
        private var value: String? = null

        suspend fun get(): String? {
            if (value == null) {
                val prefs = context.dataStore.data.first()
                val encrypted = prefs[key]
                val decrypted = encrypted?.let { encryptor.decrypt(it) }
                value = decrypted
            }
            return value
        }

        suspend fun set(input: String?) {
            context.dataStore.edit { mutablePreferences ->
                if (input == null) {
                    mutablePreferences.remove(key)
                } else {
                    mutablePreferences[key] = encryptor.encrypt(input)
                }
            }
            value = input
        }
    }

    suspend fun saveTokens(tokenPair: TokenPair) {
        saveAccessToken(tokenPair.accessToken)
        saveRefreshToken(tokenPair.refreshToken)
    }

    suspend fun saveRefreshToken(token: String) {
        refreshToken.set(token)
    }

    suspend fun saveAccessToken(token: String) {
        accessToken.set(token)
    }

    suspend fun getAccessToken(): String? {
        return accessToken.get()
    }

    suspend fun getRefreshToken(): String? {
        return refreshToken.get()
    }

    suspend fun clearTokens() {
        accessToken.set(null)
        refreshToken.set(null)
    }

    companion object {

        private const val ACCESS_TOKEN = "access_token_key"

        private const val REFRESH_TOKEN = "refresh_token_key"
    }
}