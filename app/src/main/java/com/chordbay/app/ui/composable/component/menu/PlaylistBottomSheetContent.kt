package com.chordbay.app.ui.composable.component.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.model.Song

@Composable
fun PlaylistBottomSheetContent(
    selectedSongs: List<Song>,
    onDeleteClick: () -> Unit,
    onCloseClick: () -> Unit,
    bottomPadding: Dp,
) {
    val single = selectedSongs.size == 1
    val count = selectedSongs.size
    val headerText = when {
        count == 0 -> "No song selected"
        single -> {
            val song = selectedSongs.first()
            val artist = song.artist.ifBlank { "Unknown Artist" }
            val title = song.title.ifBlank { "Untitled" }
            "$artist - $title"
        }

        else -> "$count songs selected"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp + bottomPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(
                    horizontal = 20.dp, vertical = 16.dp
                ),
                text = headerText,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                modifier = Modifier.align(Alignment.Top),
                onClick = {
                    onCloseClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionIcon(
                icon = Icons.Default.Remove,
                label = "Remove from Playlist",
                onClick = onDeleteClick
            )
        }
    }
}