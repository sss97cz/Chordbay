package com.chordbay.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStoreSettings: DataStore<Preferences> by preferencesDataStore(name = "settings")
val Context.dataStoreAuth: DataStore<Preferences> by preferencesDataStore(name = "auth_credentials")
val Context.userCredentials: DataStore<Preferences> by preferencesDataStore(name = "user_credentials")