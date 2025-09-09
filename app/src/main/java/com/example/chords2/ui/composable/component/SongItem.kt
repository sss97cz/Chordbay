package com.example.chords2.ui.composable.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Song

@Composable
fun SongItem(
    modifier: Modifier = Modifier,
    songTitle: String,
    songArtist: String,
    onSongClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = { onSongClick() }
    ) {
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
                IconButton(
                    onClick = {
                        onDeleteClick()
                    },
                    modifier = Modifier.size(20.dp),
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Edit Song"
                    )
                }
            }
            Text(text = songTitle)
            Text(text = songArtist)
        }
    }
}