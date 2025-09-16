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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.database.SongEntity
import com.example.chords2.ui.composable.component.text.SongText
import com.example.chords2.ui.composable.component.button.TransposeButton
import com.example.chords2.ui.composable.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.flow.firstOrNull
import org.koin.androidx.compose.koinViewModel

@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = koinViewModel(),
    songId: String,
    navController: NavController,
    isPost: Boolean = false,
    // setTopAppBarConfig: (String, @Composable RowScope.() -> Unit) -> Unit,
) {
    val songIdInt = songId.toIntOrNull()
    var songData by remember { mutableStateOf<SongEntity?>(null) }
    val song = songData
    val canNavigateBack = navController.previousBackStackEntry != null
    var semitones by remember { mutableIntStateOf(0) }
    val fontSize = songViewModel.songTextFontSize.collectAsState()

    LaunchedEffect(songIdInt) {
        if (songIdInt != null) {
            if (isPost){ //super temporary solution
                Log.d("SongScreen", "Post $isPost, songIdInt: $songIdInt")
                val post = songViewModel.posts.value.firstOrNull { it.id == songIdInt }
                Log.d("SongScreen", "Post $post")
                if (post == null) {
                    navController.popBackStack()
                    return@LaunchedEffect
                }
                songData = SongEntity(
                    id = post.id,
                    title = post.title,
                    artist = post.userId.toString(),
                    content = post.body
                )

            }else {
                songViewModel.getSongById(songIdInt).collect { songData = it }
            }

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
                            chordsColor = MaterialTheme.colorScheme.primary,
                            fontSize = fontSize.value
                        )
                    }
                }
            }
        }
    }
}