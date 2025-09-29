package com.example.chords2.ui.composable.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.ui.composable.component.list.AlphabeticalSongList
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

@Composable
fun PlaylistScreen(
    playlistId: Int,
    songViewModel: SongViewModel,
    navController: NavHostController
) {
    val songsFromPlaylist = songViewModel.getSongsInPlaylist(playlistId).collectAsState()
    val playlistState = songViewModel.getPlaylistById(playlistId).collectAsState()
    val playlist = playlistState.value

    val canNavigateUp = navController.previousBackStackEntry != null
    if (playlist != null) {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = playlist.name,
                    navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                    onNavigationIconClick = { if (canNavigateUp) navController.navigateUp() },
                    actions = {
                        IconButton(
                            onClick = {
                                songViewModel.deletePlaylist(playlistId)
                                if (canNavigateUp) {
                                    navController.navigateUp()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete, null
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Playlist $playlistId")
                    AlphabeticalSongList(
                        songs = songsFromPlaylist.value,
                        selectedSongs = emptyList(),
                        sortBy = SortBy.SONG_NAME,
                        onSongClick = { song ->
                            navController.navigate(Paths.SongPath.createRoute(song.localId.toString()))
                        },
                        onSongLongClick = {},
                        bottomPadding = 0.dp,
                    )
                }
            }
        }
    }
}