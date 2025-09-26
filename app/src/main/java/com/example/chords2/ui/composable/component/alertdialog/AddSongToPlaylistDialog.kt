package com.example.chords2.ui.composable.component.alertdialog

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.chords2.data.database.playlist.PlaylistEntity

@Composable
fun AddSongToPlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (playlistId: Int) -> Unit,
    playlists: List<PlaylistEntity>,
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add to Playlist") },
        text = {
            if (playlists.isEmpty()) {
                Text(text = "No playlists available. Please create a playlist first.")
            } else {
                LazyColumn {
                    items(playlists.size) { index ->
                        val playlist = playlists[index]
                        ListItem(
                            headlineContent = { Text(playlist.name) },
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    Log.d("AddSongToPlaylistDialog", "Playlist ID: ${playlist.id}")
                                    onConfirm(playlist.id)
                                }
                        )
                    }
                }
            }
        },
        confirmButton = {},
    )
}