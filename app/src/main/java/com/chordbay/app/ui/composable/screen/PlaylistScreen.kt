package com.chordbay.app.ui.composable.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chordbay.app.data.model.util.SortBy
import com.chordbay.app.ui.composable.component.list.AlphabeticalSongList
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar
import com.chordbay.app.ui.composable.navigation.Paths
import com.chordbay.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlistId: Int,
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val songsFromPlaylist = mainViewModel.getSongsInPlaylist(playlistId).collectAsState()
    val playlistState = mainViewModel.getPlaylistById(playlistId).collectAsState()
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
                                mainViewModel.deletePlaylist(playlistId)
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
                        isPlaylist = true
                    )
                }
            }
        }
    }
}