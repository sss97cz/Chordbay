package com.example.chords2.ui.composable.screen

import android.graphics.fonts.FontStyle
import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.*
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Chords
import com.example.chords2.data.model.Song
import com.example.chords2.data.parser.SongParser
import com.example.chords2.ui.composable.component.SongText
import com.example.chords2.ui.composable.component.button.AddSongButton
import com.example.chords2.ui.composable.component.button.TransposeButton
import com.example.chords2.ui.composable.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = koinViewModel(),
    songId: String,
    navController: NavController,
    // setTopAppBarConfig: (String, @Composable RowScope.() -> Unit) -> Unit,
) {
    val songIdInt = songId.toIntOrNull()
    var songData by remember { mutableStateOf<SongEntity?>(null) }
    val song = songData
    val canNavigateBack = navController.previousBackStackEntry != null
    var semitones by remember { mutableIntStateOf(0) }

    LaunchedEffect(songIdInt) {
        if (songIdInt != null) {
            songViewModel.getSongById(songIdInt).collect { songData = it }

        }
    }
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
                            .padding(16.dp)
                    ) {
                        SongText(
                            modifier = Modifier.fillMaxSize(),
                            text = song.content,
                            semitones = semitones,
                            chordsColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}