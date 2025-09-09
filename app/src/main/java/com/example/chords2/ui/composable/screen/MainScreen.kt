package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.Song // Your domain data class
import com.example.chords2.ui.composable.component.SongItem
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = koinViewModel(),
    navController: NavController
) {
    val songs = songViewModel.songs.collectAsState()
    val scope = rememberCoroutineScope()
    var enableEditing by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    scope.launch {
                        val newSongDetails = Song()
                        val newSongId = songViewModel.addNewSongAndGetId(newSongDetails)
                        navController.navigate(
                            Paths.EditSongPath.createRoute(
                                songId = newSongId.toString()
                            )
                        )
                    }
                }
            ) {
                Text(text = "Add Song")
            }
            Button(
                onClick = {
                    scope.launch {
                        songViewModel.deleteAll()
                    }
                }
            ) {
                Text(text = "Delete All Songs")
            }
            Button(
                onClick = {
                    enableEditing = !enableEditing
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (enableEditing) {
                        Color.Green
                    } else {
                        Color.Red
                    }
                )
            ) {
                Image(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Song"
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(songs.value) { songEntity ->
                SongItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    songTitle = songEntity.title,
                    songArtist = songEntity.artist,
                    onSongClick = {
                        if (!enableEditing) {
                            navController.navigate(
                                Paths.SongPath.createRoute(
                                    songEntity.id.toString()
                                )
                            )
                        }else {
                            navController.navigate(
                                Paths.EditSongPath.createRoute(
                                    songId = songEntity.id.toString()
                                )
                            )
                        }
                    },
                    onDeleteClick = {
                        scope.launch {
                            songViewModel.deleteSong(songEntity)
                        }
                    }
                )
            }
        }
    }
}
