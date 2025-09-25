package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.chords2.ui.viewmodel.SongViewModel

@Composable
fun PlaylistScreen(
    playlistId: Int,
    songViewModel: SongViewModel,
    navController: NavHostController
) {
    //val songsFromPlaylist = songViewModel
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Playlist $playlistId")
            Button(
                onClick = {
                    songViewModel.deletePlaylist(playlistId)
                    navController.popBackStack()
                }
            ){
                Text(text = "Delete Playlist")
            }
        }
    }
}