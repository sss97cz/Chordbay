package com.example.chords2.ui.composable.component.listitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ArtistItem(
    modifier: Modifier = Modifier,
    artist: String,
    songCount: Int,
    onClick: () -> Unit,
) {
//    Card(
//        modifier = modifier,
//        colors = CardDefaults.cardColors(
//            contentColor = CardDefaults.cardColors().contentColor,
//            containerColor = CardDefaults.cardColors().containerColor
//        ),
//        onClick = onClick,
//        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                modifier = Modifier
//                    .padding(horizontal = 16.dp),
//                text = title,
//                style = MaterialTheme.typography.titleMedium
//            )
//
//        }
//    }

    SongItem(
        modifier = modifier,
        songTitle = artist,
        songArtist = "$songCount song${if (songCount != 1) "s" else ""}",
        onSongClick = onClick,
        onLongClick = {},
        isSelected = false
    )
}