package com.chordbay.app.ui.composable.component.alertdialog

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.database.playlist.PlaylistEntity

@Composable
fun AddSongToPlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (playlistId: Int) -> Unit,
    playlists: List<PlaylistEntity>,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add to Playlist") },
        text = {
            if (playlists.isEmpty()) {
                Text(text = "No playlists available. Please create a playlist first.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists.size) { index ->
                        val playlist = playlists[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .clickable {
                                    Log.d("AddSongToPlaylistDialog", "Playlist ID: ${playlist.id}")
                                    onConfirm(playlist.id)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(text = playlist.name, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
    )
}