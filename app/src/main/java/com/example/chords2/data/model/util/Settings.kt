package com.example.chords2.data.model.util

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

sealed class Settings<T>(
    val title: String,
    val defaultValue: T,
    val preferencesKey: Preferences.Key<String>,
    val dilplayInSettings: Boolean = true
) {
    companion object{
        val all = listOf(
            SortBySetting, ThemeSetting, FontSize,  ColorModeSetting
        )
    }
    object SortBySetting : Settings<SortBy>(
        title = "Sort by",
        defaultValue = SortBy.SONG_NAME,
        preferencesKey = stringPreferencesKey("sort_by"),
        false,
    )
    object ThemeSetting : Settings<ThemeMode>(
        title = "Theme",
        defaultValue = ThemeMode.SYSTEM,
        preferencesKey = stringPreferencesKey("theme")
    )
    object FontSize : Settings<Int>(
        title = "Font size",
        defaultValue = 16,
        preferencesKey = stringPreferencesKey("font_size"),
        false,
    )
    object ColorModeSetting : Settings<ColorMode>(
        title = "Color Scheme",
        defaultValue = ColorMode.BLUE,
        preferencesKey = stringPreferencesKey("color_mode")
    )
}