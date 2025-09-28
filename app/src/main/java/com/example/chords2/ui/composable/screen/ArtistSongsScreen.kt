package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chords2.data.mappers.toSongUi
import com.example.chords2.ui.composable.component.listitem.RemoteSongItem
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.SongViewModel

@Composable
fun ArtistSongsScreen(
    artistName: String,
    navController: NavHostController,
    songViewModel: SongViewModel
) {
    val songsByArtist = songViewModel.getSongsByArtist(artistName).collectAsState()
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = artistName,
                onNavigationIconClick = { navController.popBackStack() },
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(songsByArtist.value) { song ->
                    RemoteSongItem(
                        song = song.toSongUi(),
                        onSongClick = {
                            navController.navigate(Paths.RemoteSongPath
                                .createRoute(song.remoteId.toString())
                            )
                        },
                        onLongClick = {},
                    )
                }
            }

        }
    }
}