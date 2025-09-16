package com.example.chords2.data.model.util

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.chords2.ui.composable.screen.SettingsScreen

sealed class Settings<T>(
    val title: String,
    val defaultValue: T,
//    val content: @Composable () -> Unit
) {
    companion object{
        val all = listOf(
            SortBySetting, ThemeSetting, FontSize
        )
    }
    object SortBySetting : Settings<SortBy>(
        title = "Sort by",
        defaultValue = SortBy.SONG_NAME,
    )
    object ThemeSetting : Settings<ThemeMode>(
        title = "Theme",
        defaultValue = ThemeMode.SYSTEM,
    )
    object FontSize : Settings<Int>(
        title = "Font size",
        defaultValue = 16,
    )
}