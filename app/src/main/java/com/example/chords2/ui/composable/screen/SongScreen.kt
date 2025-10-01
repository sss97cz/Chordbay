package com.example.chords2.ui.composable.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.Song
import com.example.chords2.ui.composable.component.text.SongText
import com.example.chords2.ui.composable.component.button.TransposeButton
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = koinViewModel(),
    songId: String,
    navController: NavController,
    isRemote: Boolean = false,
) {
    val songData by produceState<Song?>(initialValue = null, key1 = songId) {
        value = if (!isRemote && songId.toIntOrNull() != null) {
            songViewModel.getSongById(songId.toInt()).collect { songValue ->
                value = songValue
            }
        } else if (isRemote) {
            songViewModel.getRemoteSongById(songId)
        } else {
            null
        }
    }
    val song = songData
    val canNavigateBack = remember { navController.previousBackStackEntry != null }
    var semitones by remember { mutableIntStateOf(0) }
    val fontSize = songViewModel.songTextFontSize.collectAsState()

    val isDropdownMenuExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "${song?.artist ?: ""} - ${song?.title ?: ""}",
                navigationIcon = if (canNavigateBack) {
                    Icons.AutoMirrored.Filled.ArrowBack
                } else null,
                onNavigationIconClick = {
                    if (canNavigateBack) {
                        navController.navigateUp()
                    }
                },
                actions = {
                    if (song != null) {
                        val key = songViewModel.findKey(song.content)
                        if (key != null) {
                            TransposeButton(
                                initialSemitones = 0,
                                initialChord = key,
                                onUpClick = {
                                    semitones += 1
                                },
                                onDownClick = {
                                    semitones -= 1
                                }
                            )
                        }
                    }
                    Box() {
                        IconButton(
                            onClick = {
                                isDropdownMenuExpanded.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }
                        DropdownMenu(
                            expanded = isDropdownMenuExpanded.value,
                            onDismissRequest = { isDropdownMenuExpanded.value = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("+ Font Size") },
                                onClick = {
                                    if (songViewModel.songTextFontSize.value < 30) {
                                        songViewModel.setSongTextFontSize(
                                            songViewModel.songTextFontSize.value + 1
                                        )
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("- Font Size") },
                                onClick = {
                                    if (songViewModel.songTextFontSize.value > 10)
                                    songViewModel.setSongTextFontSize(
                                        songViewModel.songTextFontSize.value - 1
                                    )
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPanning ->
        Column(
            modifier = modifier
                .padding(innerPanning)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (song == null) {
                    Log.d("SongScreen", "Song with ID $songId not found.")
                    CircularProgressIndicator()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        SongText(
                            modifier = Modifier.fillMaxSize(),
                            text = song.content,
                            semitones = semitones,
                            chordsColor = MaterialTheme.colorScheme.primary,
                            fontSize = fontSize.value
                        )
                    }
                }
            }
        }
    }
}