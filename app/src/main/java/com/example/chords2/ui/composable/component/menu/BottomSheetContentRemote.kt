package com.example.chords2.ui.composable.component.menu


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chords2.data.model.Song
import com.example.chords2.ui.theme.imagevector.Download

@Composable
fun BottomSheetContentRemote(
    selectedRemoteSongs: List<Song>,
    onSaveClick: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth()
    ){
        Text(
            text = if (selectedRemoteSongs.size > 1) "${selectedRemoteSongs.size} songs selected" else selectedRemoteSongs.firstOrNull()?.title
                ?: "No song selected",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
       IconButton(
           onClick = onSaveClick,
           modifier = Modifier.weight(1f)
       ){
              Icon(imageVector = Download, contentDescription = "Save to local" )
       }
    }
}