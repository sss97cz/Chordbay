package com.example.chords2.ui.composable.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.Song
import com.example.chords2.ui.composable.component.textfield.SongContentEditor
import com.example.chords2.ui.composable.component.textfield.SongTextField
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditSongScreen(
    modifier: Modifier = Modifier,
    songId: String,
    navController: NavController,
    songViewModel: SongViewModel = koinViewModel(),
) {
    // TODO("needs fixing")
    LaunchedEffect(Unit) {
        songViewModel.clearSongStates()
    }

    DisposableEffect(Unit) {
        onDispose {
            navController.navigateUp()
        }
    }

    Log.d("EditSongScreen", "Screen started. Received songId: $songId")

    val songName by songViewModel.songName.collectAsState()
    val songArtist by songViewModel.songArtist.collectAsState()
    val songContent by songViewModel.songContent.collectAsState()



    val currentSongDbId: Int? = remember(songId) { songId.toIntOrNull() }
    Log.d("EditSongScreen", "Current song DB ID: $currentSongDbId")
    val songIdInt = remember(songId) {
        if (songId != "new") {
            songId.toIntOrNull()
        } else {
            null
        }
    }

    val songData by produceState<Song?>(initialValue = null, key1 = songIdInt) {
        if (songIdInt != null) {
            songViewModel.getSongById(songIdInt).collect { songValue ->
                value = songValue
            }
        } else {
            if (songId == "new") {
                value = Song(localId = null, remoteId = null, title = "", artist = "", content = "")
            }
        }
        Log.d("EditSongScreen", "Song data loaded: ${value ?: "null"}")
    }
    val song = songData
    LaunchedEffect(song) {
        if (song != null && songName == null) {
            songViewModel.setSongName(song.title)
            songViewModel.setSongArtist(song.artist)
            songViewModel.setSongContent(TextFieldValue(song.content))
            Log.d("EditSongScreen", "States updated from loaded song: name='${songName}'")
        } else if (songId == "new") {
            songViewModel.setSongName("")
            songViewModel.setSongArtist("")
            songViewModel.setSongContent(TextFieldValue(""))
            Log.d("EditSongScreen", "States initialized for new song.")
        }
    }
    val canNavigateBack = navController.previousBackStackEntry != null
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Song Editor",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                navigationIconContentDescription = if (canNavigateBack) "Back" else null,
                onNavigationIconClick = if (canNavigateBack) { {
                        navController.navigateUp()
                    }
                } else null, actions = {
                    IconButton(onClick = {
                        if (song != null) {
                            if (songId == "new") {
                                songViewModel.insertSong(
                                    Song(
                                        localId = null,
                                        remoteId = song.remoteId,
                                        title = songName ?: "",
                                        artist = songArtist,
                                        content = songContent.text
                                    )
                                )
                                navController.navigateUp()
                            } else if (currentSongDbId != null) {
                                songViewModel.updateSong(
                                    Song(
                                        localId = currentSongDbId,
                                        remoteId = song.remoteId,
                                        title = songName ?: "",
                                        artist = songArtist,
                                        content = songContent.text
                                    )
                                )
                                navController.navigateUp()
                            }
                        }
                        Log.d(
                            "EditSongScreen",
                            "Save button clicked. Song saved: name='${songName}'"
                        )
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SongTextField(
                    modifier = Modifier.weight(1f),
                    value = songName ?: "",
                    onValueChange = { songViewModel.setSongName(it) },
                    singleLine = true,
                    label = "Song Title"
                )
                SongTextField(
                    modifier = Modifier.weight(1f),
                    value = songArtist,
                    onValueChange = { songViewModel.setSongArtist(it) },
                    singleLine = true,
                    label = "Artist"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SongContentEditor(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    value = songContent,
                    onValueChange = { songViewModel.setSongContent(it) },
                )
            }
        }
    }
}
