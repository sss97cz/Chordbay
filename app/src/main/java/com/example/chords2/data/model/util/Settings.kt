package com.example.chords2.data.model.util

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed class Settings<T>(
    val title: String,
    val defaultValue: T,
    val content: @Composable () -> Unit
) {
    companion object{
        val all = listOf(
            SortBySetting, ThemeSetting
        )
    }
    object SortBySetting : Settings<SortBy>(
        title = "Sort by",
        defaultValue = SortBy.SONG_NAME,
        content = { Button({}) {Text("hahaahlsdjkljjkj")} }
    )
    object ThemeSetting : Settings<Boolean>(
        title = "Theme",
        defaultValue = true,
        content = { Button({}) {Text("theme change")} }

    )
}