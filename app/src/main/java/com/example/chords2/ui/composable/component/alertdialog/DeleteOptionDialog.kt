package com.example.chords2.ui.composable.component.alertdialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chords2.data.helper.pluralText
import com.example.chords2.data.model.Song

@Composable
fun DeleteOptionDialog(
    songs: List<Song>,
    onDismiss: () -> Unit,
    onDelete: (deleteAction: Map<Int, Pair<Boolean, Boolean>>) -> Unit
) {
    // Defaults for all songs
    var deleteLocal by remember { mutableStateOf(true) }
    var deleteRemote by remember { mutableStateOf(false) }

    // Per-item overrides: localId -> (local, remote)
    val deleteAction = remember { mutableStateMapOf<Int, Pair<Boolean, Boolean>>() }

    // Confirm is enabled if at least one song would be deleted (after applying defaults + overrides)
    val canConfirm by remember(songs, deleteLocal, deleteRemote, deleteAction) {
        derivedStateOf {
            songs.any { song ->
                val key = song.localId ?: return@any false
                val pair = deleteAction[key] ?: Pair(deleteLocal, deleteRemote)
                val local = pair.first
                val remoteCandidate = pair.second
                val remoteAllowed = !song.remoteId.isNullOrBlank()
                val remote = remoteCandidate && remoteAllowed
                local || remote
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete ${pluralText("song", songs.size)}") },
        confirmButton = {
            TextButton(
                onClick = {
                    // Build a complete per-song map by merging defaults with overrides
                    val final = buildMap<Int, Pair<Boolean, Boolean>> {
                        songs.forEach { song ->
                            val id = song.localId ?: return@forEach
                            val override = deleteAction[id] ?: Pair(deleteLocal, deleteRemote)
                            val local = override.first
                            // Clamp remote to false if the song isn't posted
                            val remote =
                                if (song.remoteId.isNullOrBlank()) false else override.second
                            put(id, Pair(local, remote))
                        }
                    }
                    onDelete(final)
                },
                enabled = canConfirm
            ) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Select delete options for ${songs.size} ${pluralText("song", songs.size)}.")
                Text("Default options:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = deleteLocal,
                        onClick = {
                            deleteLocal = !deleteLocal
                            deleteAction.map { (key, pair) ->
                                deleteAction[key] = Pair(deleteLocal, pair.second)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = { Text("Local") }
                    )
                    FilterChip(
                        selected = deleteRemote, // FIX: use deleteRemote here
                        onClick = {
                            deleteRemote = !deleteRemote
                            deleteAction.map { (key, pair) ->
                                deleteAction[key] = Pair(pair.first, deleteRemote)
                            }
                        }, // FIX: toggle deleteRemote, not deleteLocal
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = { Text("Remote") }
                    )
                }

                Text("You can customize the delete options for each song below.")
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(songs, key = { it.localId ?: it.hashCode() }) { song ->
                        val key = song.localId ?: return@items
                        val current = deleteAction[key] ?: Pair(deleteLocal, deleteRemote)
                        val remoteEnabled = song.markSynced

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = buildString {
                                    append(song.artist.ifBlank { "Unknown Artist" })
                                    append(" - ")
                                    append(song.title.ifBlank { "Untitled" })
                                },
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.weight(0.1f))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                                FilterChip(
                                    selected = current.first,
                                    onClick = {
                                        deleteAction[key] = Pair(!current.first, current.second)
                                    },
//                                    leadingIcon = {
//                                        Icon(
//                                            imageVector = Icons.Default.Delete,
//                                            contentDescription = null,
//                                            tint = MaterialTheme.colorScheme.onSurface
//                                        )
//                                    },
                                    label = { Text("Local") }
                                )
                                FilterChip(
                                    enabled = remoteEnabled, // disable if not posted
                                    selected = current.second && remoteEnabled,
                                    onClick = {
                                        if (remoteEnabled) {
                                            deleteAction[key] = Pair(current.first, !current.second)
                                        }
                                    },
//                                    leadingIcon = {
//                                        Icon(
//                                            imageVector = Icons.Default.Delete,
//                                            contentDescription = null,
//                                            tint = MaterialTheme.colorScheme.onSurface
//                                        )
//                                    },
                                    label = { Text("Remote") }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun DeleteDialogPrev() {
    DeleteOptionDialog(
        songs = listOf(
            Song(
                localId = 1,
                remoteId = "abc",
                title = "Song 1",
                artist = "Artist 1",
                content = "Content 1"
            ),
            Song(
                localId = 2,
                remoteId = "",
                title = "Song 2sdjnvckjdsanvkjdsnkjdsnkjdsan",
                artist = "Artist 2",
                content = "Content 2"
            )
        ),
        onDismiss = {},
        onDelete = {}
    )
}