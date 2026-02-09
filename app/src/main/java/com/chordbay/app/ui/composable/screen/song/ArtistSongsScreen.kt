package com.chordbay.app.ui.composable.screen.song

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chordbay.app.data.helper.pluralText
import com.chordbay.app.ui.composable.component.listitem.RemoteSongItem
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar
import com.chordbay.app.ui.composable.navigation.Paths
import com.chordbay.app.ui.viewmodel.RemoteSongsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSongsScreen(
    artistName: String,
    navController: NavHostController,
    remoteSongsViewModel: RemoteSongsViewModel
) {
    val canNavigateUp = navController.previousBackStackEntry != null
    val songs =  remoteSongsViewModel.artistSongs.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveSuccess = remoteSongsViewModel.saveSuccess.collectAsState()
    var showMenu by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        remoteSongsViewModel.getSongsByArtist(artistName)
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MyTopAppBar(
                title = artistName,
                subtitle = pluralText(count = songs.value.size,
                    msg = if(songs.value.isEmpty()) "no songs" else "${songs.value.size} song"
                ),
                onNavigationIconClick = {
                    if (canNavigateUp) {
                        navController.navigateUp()
                    }
                },
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                actions = {
                    Box() {
                        IconButton(
                            onClick = {
                                showMenu = !showMenu
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    showMenu = false
                                    remoteSongsViewModel.saveAllSongs(songs.value)
                                },
                                text = {
                                    Text("Download all",)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null
                                    )
                                }
                            )

                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(4.dp)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(songs.value) { song ->
                    RemoteSongItem(
                        songTitle = song.title,
                        songArtist = song.artist,
                        isSynced = song.markSynced,
                        onSongClick = {
                            remoteSongsViewModel.clearSaveSuccess()
                            navController.navigate(
                                Paths.RemoteSongPath.createRoute(song.remoteId ?: "")
                            )
                        },
                        onLongClick = {},
                        onDownloadClick = {
                            remoteSongsViewModel.saveSong(song)
                            scope.launch {
                                if (snackbarHostState.currentSnackbarData != null) {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                                snackbarHostState.showSnackbar(
                                    message = "\"${song.title}\" downloaded"
                                )
                                if (saveSuccess.value == true) {
                                    remoteSongsViewModel.clearSaveSuccess()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}