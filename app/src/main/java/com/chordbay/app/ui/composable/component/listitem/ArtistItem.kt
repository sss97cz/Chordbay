package com.chordbay.app.ui.composable.component.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chordbay.app.data.helper.pluralText

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
    )
}