package com.chordbay.app.ui.composable.screen.song

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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

    LaunchedEffect(Unit) {
        remoteSongsViewModel.getSongsByArtist(artistName)
    }

//    val scaffoldState = rememberBottomSheetScaffoldState(
//        bottomSheetState = rememberStandardBottomSheetState(
//            initialValue = SheetValue.Hidden,
//            skipHiddenState = false
//        )
//    )
//    val sheetPeekHeight by remember(
//        selectedRemoteSongsList.isNotEmpty(),
//    ) {
//        derivedStateOf {
//            if (selectedRemoteSongsList.isNotEmpty()) {
//                BottomSheetDefaults.SheetPeekHeight
//            } else {
//                0.dp
//            }
//        }
//    }
//    val targetPadding by remember(scaffoldState.bottomSheetState.currentValue) {
//        derivedStateOf {
//            when (scaffoldState.bottomSheetState.currentValue) {
//                SheetValue.Expanded -> 190.dp
//                SheetValue.PartiallyExpanded -> {
//                    if (selectedRemoteSongsList.isNotEmpty()) 26.dp else 0.dp
//                }
//                else -> 0.dp
//            }
//        }
//    }
//    val dynamicBottomPadding by animateDpAsState(
//        targetValue = targetPadding,
//        animationSpec = tween(durationMillis = 60)
//    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MyTopAppBar(
                title = artistName,
                onNavigationIconClick = {
                    if (canNavigateUp) {
                        navController.navigateUp()
                    }
                },
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack
            )
        },
//        sheetContent = {
//            BottomSheetContentRemote(
//                selectedRemoteSongs = selectedRemoteSongsList,
//                onSaveClick = {
//                    scope.launch {
//                        remoteSongsViewModel.saveSelectedRemoteSongsToDatabase()
//                        scaffoldState.bottomSheetState.hide()
//                        remoteSongsViewModel.clearSelectedRemoteSongs()
//                    }
//                    Toast.makeText(
//                        navController.context,
//                        "${pluralText("song", selectedRemoteSongsList.count())} saved to database",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            )
//        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(4.dp)
                .fillMaxSize()
        ) {
//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
////                contentPadding = PaddingValues(
////                    bottom = dynamicBottomPadding
////                )
//            ) {
//                items(songs) { song ->
//                    RemoteSongItem(
//                        songTitle = song.title,
//                        songArtist = song.artist,
//                        isSynced = song.markSynced,
//                        onSongClick = {
//                            navController.navigate(
//                                Paths.RemoteSongPath.createRoute(song.remoteId ?: "")
//                            )
//                        },
//                        onLongClick = {},
//                        onDownloadClick = {}
//                    )
//                }
//            }
//        }
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
                            }
                        }
                    )
                }
            }
        }
    }
}