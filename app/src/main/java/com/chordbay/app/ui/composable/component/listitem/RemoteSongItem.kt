package com.chordbay.app.ui.composable.component.listitem

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chordbay.app.ui.theme.imagevector.Download


@Composable
fun RemoteSongItem(
    modifier: Modifier = Modifier,
    songTitle: String,
    songArtist: String,
    isSynced: Boolean,
    onSongClick: () -> Unit,
    onLongClick: () -> Unit,
    onDownloadClick: () -> Unit,
    isSelected: Boolean = false,
){
    SongItem(
        modifier = modifier,
        songTitle = songTitle,
        songArtist = songArtist,
        onSongClick = onSongClick,
        onLongClick = onLongClick,
        isSelected = isSelected,
        trailingContent = {
            IconButton(
                onClick = {
                    if (!isSynced) {
                        onDownloadClick()
                    }
                },
                modifier = Modifier.size(24.dp)
            ) {
                if (isSynced) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Downloaded",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Download,
                        contentDescription = "Download",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}