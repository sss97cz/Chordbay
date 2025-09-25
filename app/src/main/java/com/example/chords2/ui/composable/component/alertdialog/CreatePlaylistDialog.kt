package com.example.chords2.ui.composable.component.alertdialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CreatePlaylistDialog(
    onDismissRequest: () -> Unit,
    onCreatePlaylist: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Create Playlist") },
        text = {
            TextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Playlist Name") }
            )
        },
        confirmButton = {
            IconButton(
                onClick = { onCreatePlaylist(playlistName) }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Playlist")
            }
        },
        dismissButton = {
            IconButton(onClick = onDismissRequest) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
            }
        }
    )
}

