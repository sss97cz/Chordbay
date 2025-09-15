package com.example.chords2.data.model.util

import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

sealed class Settings<T>(
    val title: String,
    val defaultValue: T,
//    val content: @Composable () -> Unit
) {
    companion object{
        val all = listOf(
            SortBySetting, ThemeSetting
        )
    }
    object SortBySetting : Settings<SortBy>(
        title = "Sort by",
        defaultValue = SortBy.SONG_NAME,
//        content = {
//            DropdownMenu(
//                expanded = false,
//                onDismissRequest = {
//                }
//            ) {
//                DropdownMenu(expanded = false, onDismissRequest = { }) {
//                    DropdownMenuItem(
//                        text = { Text("Song Name") },
//                        onClick = { }
//                    )
//                    DropdownMenuItem(
//                        text = { Text("Artist Name") },
//                        onClick = { }
//                    )
//                }
//            }
//        }
    )
    object ThemeSetting : Settings<Boolean>(
        title = "Theme",
        defaultValue = true,
//        content = { Button({}) {Text("theme change")} }

    )
}