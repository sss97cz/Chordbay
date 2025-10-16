package com.example.chords2.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.chords2.data.model.TokenPair
import kotlinx.coroutines.flow.first

/**
 * DataStore instance for storing secure credentials.
 */
private val Context.dataStore by preferencesDataStore(name = "auth_credentials")

/**
 * Manages secure storage and retrieval of user credentials (access and refresh tokens).
 *
 * This class uses Android Keystore to generate a secret key for encrypting and decrypting
 * tokens, which are then stored in Jetpack DataStore.
 *
 * @property context The application context, used for accessing DataStore and KeyStore.
 */
class CredentialManager(private val context: Context) {

    private val encryptor: Encryptor by lazy { Encryptor() }

    private var refreshToken = DataStoreEntity(stringPreferencesKey(REFRESH_TOKEN))
    private var accessToken = DataStoreEntity(stringPreferencesKey(ACCESS_TOKEN))

    /**
     * An inner class to handle a single entity in DataStore.
     * It provides methods to get and set a string value associated with a given DataStore key,
     * handling encryption and decryption.
     *
     * @property key The [Preferences.Key] for this DataStore entity.
     */
    inner class DataStoreEntity(private val key: Preferences.Key<String>) {
        private var value: String? = null

        /**
         * Retrieves the decrypted value from DataStore.
         * If the value is not already cached in memory, it reads from DataStore,
         * decrypts it, and then caches it.
         *
         * @return The decrypted string value, or null if not found or an error occurs.
         */
        suspend fun get(): String? {
            if (value == null) {
                val prefs = context.dataStore.data.first()
                val encrypted = prefs[key]
                val decrypted = encrypted?.let { encryptor.decrypt(it) }
                value = decrypted
            }
            return value
        }

        /**
         * Encrypts and saves the given input string to DataStore.
         * If the input is null, it removes the corresponding key from DataStore.
         * The value is also updated in the in-memory cache.
         *
         * @param input The string value to encrypt and save, or null to remove the entry.
         */
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

    /**
     * Saves the refresh token to secure storage.
     * @param token The refresh token to be saved.
     */
    suspend fun saveRefreshToken(token: String) {
        refreshToken.set(token)
    }

    /**
     * Saves the access token to secure storage.
     * @param accessToken The access token to be saved.
     */
    suspend fun saveAccessToken(token: String) {
        accessToken.set(token)
    }

    /**
     * Retrieves the access token from secure storage.
     * @return The access token, or null if not found.
     */
    suspend fun getAccessToken(): String? {
        return accessToken.get()
    }

    /**
     * Retrieves the refresh token from secure storage.
     * @return The refresh token, or null if not found.
     */
    suspend fun getRefreshToken(): String? {
        return refreshToken.get()
    }

    /**
     * Clears both access and refresh tokens from secure storage.
     */
    suspend fun clearTokens() {
        accessToken.set(null)
        refreshToken.set(null)
    }

    /**
     * Companion object for [CredentialManager].
     * Contains constants for DataStore keys.
     */
    companion object {
        /**
         * DataStore key for the access token.
         */
        private const val ACCESS_TOKEN = "access_token_key"
        /**
         * DataStore key for the refresh token.
         */
        private const val REFRESH_TOKEN = "refresh_token_key"
    }
}