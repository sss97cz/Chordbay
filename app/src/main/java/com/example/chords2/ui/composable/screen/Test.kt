package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.chords2.data.model.Song

class SongViewModel{
    val songs = kotlinx.coroutines.flow.MutableStateFlow<List<Song>>(emptyList())
    fun loadSongs(){
        songs.value = listOf(
            Song(title = "Song A", artist = "Artist 1", content = ""),
            Song( title = "Song B", artist = "Artist 2", content = ""),
            Song( title = "Song C", artist = "Artist 3", content = ""),
        )
    }
}

@Composable
fun SongScreen(
    songViewModel: SongViewModel
){
    // Sledujeme StateFlow z ViewModelu a konvertujeme ho na State
    val songs = songViewModel.songs.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        // Tlačítko pro načtení seznamu skladeb z repository přes ViewModel
        Button(
            onClick = { songViewModel.loadSongs() }
        ){
            Text( text = "Load songs" )
        }
        // Zobrazení seznamu skladeb pomocí LazyColumn
        SongList(
            modifier = Modifier.fillMaxWidth(),
            songs = songs.value // předání aktuálního stavu písní
        )
    }
}

@Composable
fun SongList(
    modifier: Modifier = Modifier,
    songs: List<Song>,
){
// LazyColumn je scrollovatelný seznam, renderuje jen viditelné položky
    LazyColumn(
        modifier = modifier
    ) {
        items(songs) { song ->
            ListItem(
                headlineContent = { Text(song.title) },
                supportingContent = { Text("Artist:{song.artist}") }
            )
        }
    }
}