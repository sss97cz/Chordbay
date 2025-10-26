package com.example.chords2.ui.composable.component.listitem


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.chords2.data.helper.pluralText

@Composable
fun ArtistItem(
    modifier: Modifier = Modifier,
    artist: String,
    songCount: Int,
    onClick: () -> Unit,
) {
    SongItem(
        modifier = modifier,
        songTitle = artist,
        songArtist = pluralText(msg = "$songCount song", count = songCount),
        onSongClick = onClick,
        onLongClick = {},
        isSelected = false,
        isSynced = false
    )
}