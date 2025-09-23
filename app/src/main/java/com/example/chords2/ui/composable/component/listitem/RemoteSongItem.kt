package com.example.chords2.ui.composable.component.listitem

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chords2.data.model.SongUi
import com.example.chords2.ui.theme.imagevector.Download

@Composable
fun RemoteSongItem(
    modifier: Modifier = Modifier,
    song: SongUi,
    onSongClick: (SongUi) -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
) {
    Card(
        modifier = modifier.combinedClickable(
            onClick = { onSongClick(song) },
            onLongClick = onLongClick
        ),
        colors = CardDefaults.cardColors().copy(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.tertiaryContainer else CardDefaults.cardColors().containerColor,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onTertiaryContainer else CardDefaults.cardColors().contentColor
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
            }
            Text(text = song.artist)
            Text(text = song.title)
        }
    }
}