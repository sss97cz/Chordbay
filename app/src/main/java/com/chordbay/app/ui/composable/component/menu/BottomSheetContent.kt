package com.chordbay.app.ui.composable.component.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.model.Song
import com.chordbay.app.ui.theme.imagevector.Playlist_add

@Composable
fun BottomSheetContent(
    bottomPadding: Dp,
    selectedSongs: List<Song>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPostClick: () -> Unit,
    onCloseClick: () -> Unit,
    onAddToPlaylistClick: () -> Unit,
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
                ).widthIn(max = 280.dp),
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
            if (single) {
                ActionIcon(
                    icon = Icons.Default.Edit,
                    label = "Edit",
                    onClick = onEditClick
                )
            }
            ActionIcon(
                icon = Icons.Default.Delete,
                label = "Delete",
                onClick = onDeleteClick
            )

            ActionIcon(
                icon = Playlist_add,
                label = "Add to Playlist",
                onClick = onAddToPlaylistClick
            )
            if (count > 0) {
                ActionIcon(
                    icon = Icons.Default.Share,
                    label = "Share",
                    onClick = onPostClick
                )
            }
        }
    }
}

@Composable
fun ActionIcon(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .widthIn(min = 72.dp)
            .semantics(mergeDescendants = true) { contentDescription = label }
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}
