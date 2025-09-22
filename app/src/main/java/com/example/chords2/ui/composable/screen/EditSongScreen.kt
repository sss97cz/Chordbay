package com.example.chords2.ui.composable.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.chords2.data.database.SongEntity // Import SongEntity
import com.example.chords2.data.model.Song
import com.example.chords2.ui.composable.component.textfield.SongContentEditor
import com.example.chords2.ui.composable.component.textfield.SongTextField
import com.example.chords2.ui.composable.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.flow.first // To get the first value from StateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditSongScreen(
    modifier: Modifier = Modifier,
    songId: String,
    navController: NavController,
    songViewModel: SongViewModel = koinViewModel(),
    //   setTopAppBarConfig: (String, @Composable RowScope.() -> Unit) -> Unit
) {
    Log.d("EditSongScreen", "Screen started. Received songId (String?): $songId")

    var songName by rememberSaveable { mutableStateOf("") }
    var songArtist by rememberSaveable { mutableStateOf("") }
    var songContent by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }


    val currentSongDbId: Int? = remember(songId) { songId.toIntOrNull() }
    val songIdInt = remember(songId) { songId.toIntOrNull() }

    val songData by produceState<Song?>(initialValue = null, key1 = songIdInt) {
        if (songIdInt != null) {
            songViewModel.getSongById(songIdInt).collect { songValue ->
                value = songValue
            }
        } else {
            value = null
        }
    }
    val song = songData
    if (song != null) {
        LaunchedEffect(songId) {
            songName = song.title
            songArtist = song.artist
            songContent = TextFieldValue(song.content)
            Log.d("EditSongScreen", "States updated from loaded song: name='${songName}'")
        }
    }
    val canNavigateBack = navController.previousBackStackEntry != null
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Song Editor",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                navigationIconContentDescription = if (canNavigateBack) "Back" else null,
                onNavigationIconClick = if (canNavigateBack) {
                    { navController.navigateUp() }
                } else null, actions = {
                    IconButton(onClick = {
                        // TODO: Implement save logic using songViewModel
                        if (song != null) {
                            if (currentSongDbId != null) {
                                val updatedSong = Song(
                                    localId = currentSongDbId,
                                    title = songName,
                                    artist = songArtist,
                                    content = songContent.text
                                )
                                songViewModel.updateSong(updatedSong)
                            }
                        }
                        navController.popBackStack() // Go back after saving
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
                    value = songName,
                    onValueChange = { songName = it },
                    singleLine = true,
                    label = "Song Title"
                )
                SongTextField(
                    modifier = Modifier.weight(1f),
                    value = songArtist,
                    onValueChange = { songArtist = it },
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
                    onValueChange = { songContent = it },
                )
            }
        }
    }
}
