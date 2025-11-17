package com.example.chords2.data.model.util

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

sealed class Settings<T>(
    val title: String,
    val defaultValue: T,
    val preferencesKey: Preferences.Key<String>,
    val displayInSettings: Boolean = true
) {
    abstract fun parse(raw: String?): T
    object SortBySetting : Settings<SortBy>(
        title = "Sort by",
        defaultValue = SortBy.SONG_NAME,
        preferencesKey = stringPreferencesKey("sort_by"),
        false,
    ){
        override fun parse(raw: String?): SortBy =
            SortBy.valueOf(raw ?: defaultValue.name)
    }
    object ThemeSetting : Settings<ThemeMode>(
        title = "Theme",
        defaultValue = ThemeMode.SYSTEM,
        preferencesKey = stringPreferencesKey("theme")
    ){
        override fun parse(raw: String?): ThemeMode =
            ThemeMode.valueOf(raw ?: defaultValue.name)
    }
    object FontSize : Settings<Int>(
        title = "Font size",
        defaultValue = 16,
        preferencesKey = stringPreferencesKey("font_size"),
        false,
    ){
        override fun parse(raw: String?): Int =
            (raw ?: defaultValue.toString()).toInt()
    }
    object ColorModeSetting : Settings<ColorMode>(
        title = "Color Scheme",
        defaultValue = ColorMode.BLUE,
        preferencesKey = stringPreferencesKey("color_mode")
    ){
        override fun parse(raw: String?): ColorMode =
            ColorMode.valueOf(raw ?: defaultValue.name)
    }

    companion object{
        val all = listOf(
            SortBySetting, ThemeSetting, FontSize,  ColorModeSetting
        )
    }
}