package com.example.chords2.ui.composable.screen

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chords2.data.helper.pluralText
import com.example.chords2.ui.composable.component.listitem.RemoteSongItem
import com.example.chords2.ui.composable.component.menu.BottomSheetContentRemote
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.launch
import kotlin.collections.isNotEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistSongsScreen(
    artistName: String,
    navController: NavHostController,
    songViewModel: SongViewModel
) {
    LaunchedEffect(Unit) {
        songViewModel.clearSelectedRemoteSongs()
    }
    DisposableEffect(Unit) {
        onDispose {
            songViewModel.clearSelectedRemoteSongs()
        }
    }
    val canNavigateUp = navController.previousBackStackEntry != null
    val songsByArtist = remember { songViewModel.getSongsByArtist(artistName) }
    val songs by songsByArtist.collectAsState()
    val selectedRemoteSongsList by songViewModel.selectedRemoteSongs.collectAsState()

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val sheetPeekHeight by remember(
        selectedRemoteSongsList.isNotEmpty(),
    ) {
        derivedStateOf {
            if (selectedRemoteSongsList.isNotEmpty()) {
                BottomSheetDefaults.SheetPeekHeight
            } else {
                0.dp
            }
        }
    }
    val targetPadding by remember(scaffoldState.bottomSheetState.currentValue) {
        derivedStateOf {
            when (scaffoldState.bottomSheetState.currentValue) {
                SheetValue.Expanded -> 190.dp
                SheetValue.PartiallyExpanded -> {
                    if (selectedRemoteSongsList.isNotEmpty()) 26.dp else 0.dp
                }
                else -> 0.dp
            }
        }
    }
    val dynamicBottomPadding by animateDpAsState(
        targetValue = targetPadding,
        animationSpec = tween(durationMillis = 60)
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = sheetPeekHeight,
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
        sheetContent = {
            BottomSheetContentRemote(
                selectedRemoteSongs = selectedRemoteSongsList,
                onSaveClick = {
                    scope.launch {
                        songViewModel.saveSelectedRemoteSongsToDatabase()
                        scaffoldState.bottomSheetState.hide()
                        songViewModel.clearSelectedRemoteSongs()
                    }
                    Toast.makeText(
                        navController.context,
                        "${pluralText("song", selectedRemoteSongsList.count())} saved to database",
                        Toast.LENGTH_SHORT
                    ).show()
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    bottom = dynamicBottomPadding
                )
            ) {
                items(songs) { song ->
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
                        onDownloadClick = {}
                    )
                }
            }
        }
    }
}