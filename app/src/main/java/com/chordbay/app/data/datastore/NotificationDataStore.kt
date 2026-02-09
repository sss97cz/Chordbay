package com.chordbay.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationDataStore(context: Context) {
    private val dataStore = context.notificationDataStore

    companion object {
        private val SEEN_NOTIFICATION_IDS_KEY = stringSetPreferencesKey("seen_notification_ids")
    }

    suspend fun markNotificationAsSeen(notificationId: String) {
        dataStore.edit { preferences ->
            val currentIds = preferences[SEEN_NOTIFICATION_IDS_KEY] ?: emptySet()
            preferences[SEEN_NOTIFICATION_IDS_KEY] = currentIds + notificationId
        }
    }

    fun getSeenNotificationIds(): Flow<Set<String>> {
        return dataStore.data.map { preferences ->
            preferences[SEEN_NOTIFICATION_IDS_KEY] ?: emptySet()
        }
    }

    suspend fun clearSeenNotifications() {
        dataStore.edit { preferences ->
            preferences.remove(SEEN_NOTIFICATION_IDS_KEY)
        }
    }
}

