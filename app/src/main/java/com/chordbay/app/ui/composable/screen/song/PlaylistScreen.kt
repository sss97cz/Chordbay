package com.chordbay.app.ui.composable.screen.song

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chordbay.app.data.helper.pluralText
import com.chordbay.app.data.model.Song
import com.chordbay.app.ui.composable.component.list.PlaylistList
import com.chordbay.app.ui.composable.component.menu.PlaylistBottomSheetContent
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar
import com.chordbay.app.ui.composable.navigation.Paths
import com.chordbay.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlistId: Int,
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val songsFromPlaylist = mainViewModel.playlistSongs.collectAsState()
    val playlistState = mainViewModel.getPlaylistById(playlistId).collectAsState()
    val selectedSongsList = rememberSaveable { mutableStateOf<List<Song>>(emptyList()) }
    val playlist = playlistState.value
    LaunchedEffect(Unit) {
        mainViewModel.getSongsInPlaylist(playlistId)
    }

    val canNavigateUp = navController.previousBackStackEntry != null
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    // Dynamic bottom padding for LazyColumn
    val targetPadding by remember(scaffoldState.bottomSheetState.currentValue) {
        derivedStateOf {
            when (scaffoldState.bottomSheetState.currentValue) {
                SheetValue.Expanded -> 190.dp
                SheetValue.PartiallyExpanded -> {
                    if (selectedSongsList.value.isNotEmpty()) 64.dp else 24.dp
                }
                else -> 24.dp
            }
        }
    }
    val bottomSystemPadding = WindowInsets.navigationBars.asPaddingValues()
    val dynamicBottomPadding by animateDpAsState(
        targetValue = targetPadding + bottomSystemPadding.calculateBottomPadding(),
        animationSpec = tween(durationMillis = 60)
    )
    val sheetPeekHeight by remember(
        selectedSongsList.value.isNotEmpty(),
        scaffoldState.bottomSheetState.currentValue
    ) {
        derivedStateOf {
            if (selectedSongsList.value.isNotEmpty()) {
                BottomSheetDefaults.SheetPeekHeight
            } else {
                0.dp
            }
        }
    }
    val isMenuExpanded = remember { mutableStateOf(false) }
    val isRenameDialogVisible = remember { mutableStateOf(false) }
    if (playlist != null) {
        BottomSheetScaffold(
            sheetPeekHeight = sheetPeekHeight,
            scaffoldState = scaffoldState,
            topBar = {
                MyTopAppBar(
                    title = playlist.name,
                    subtitle = if (songsFromPlaylist.value.isNotEmpty()) {
                        pluralText(
                            "${songsFromPlaylist.value.size} song",
                            songsFromPlaylist.value.size
                        )
                    } else {
                        "No songs"
                    },
                    navigationIcon = Icons.AutoMirrored.Default.ArrowBack,
                    onNavigationIconClick = { if (canNavigateUp) navController.navigateUp() },
                    actions = {

                        Box {
                            IconButton(
                                onClick = {
                                    isMenuExpanded.value = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert, null
                                )
                            }
                            DropdownMenu(
                                expanded = isMenuExpanded.value,
                                onDismissRequest = { isMenuExpanded.value = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Rename Playlist") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        isRenameDialogVisible.value = true
                                        isMenuExpanded.value = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Playlist") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        isMenuExpanded.value = false
                                        mainViewModel.deletePlaylist(playlistId)
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                )
            },
            sheetContent = {
                PlaylistBottomSheetContent(
                    selectedSongs = selectedSongsList.value,
                    onCloseClick = {
                        selectedSongsList.value = emptyList()
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                        }
                    },
                    onDeleteClick = {
                        val songsToDelete = selectedSongsList.value
                        songsToDelete.forEach { song ->
                            if (song.localId != null) {
                                mainViewModel.removeSongFromPlaylist(
                                    playlistId,
                                    song.localId
                                )
                            }
                        }
                        selectedSongsList.value = emptyList()
                        scope.launch {
                            scaffoldState.bottomSheetState.hide()
                        }
                    },
                    bottomPadding = bottomSystemPadding.calculateBottomPadding()
                )
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PlaylistList(
                        selectedSongsList = selectedSongsList.value,
                        songs = songsFromPlaylist.value,
                        onMove = { fromIndex, toIndex ->
                            mainViewModel.moveSongInPlaylist(
                                playlistId,
                                fromIndex,
                                toIndex
                            )
                        },
                        onSongClick = { song ->
                            if (selectedSongsList.value.isNotEmpty()) {
                                if (selectedSongsList.value.contains(song)) {
                                    selectedSongsList.value =
                                        selectedSongsList.value.toMutableList().also {
                                            it.remove(song)
                                        }
                                } else {
                                    selectedSongsList.value += song
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            } else {
                                navController.navigate(
                                    Paths.SongFromPlaylistPath.createRoute(
                                        song.localId.toString(),
                                        playlistId
                                    )
                                )
                            }
                        },
                        onSongLongClick = {
                            if (selectedSongsList.value.contains(it)) {
                                selectedSongsList.value =
                                    selectedSongsList.value.toMutableList().also { list ->
                                        list.remove(it)
                                    }
                            } else {
                                selectedSongsList.value += it
                            }
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        bottomPadding = dynamicBottomPadding
                    )
                }
            }
        }
        RenamePlaylistDialog(
            currentName = playlist.name,
            isVisible = isRenameDialogVisible.value,
            playlistId = playlistId,
            onDismissRequest = { isRenameDialogVisible.value = false },
            onRenameClick = { newName ->
                mainViewModel.renamePlaylist(playlistId, newName)
            }
        )
    }
}

@Composable
fun RenamePlaylistDialog(
    currentName: String = "",
    playlistId: Int,
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onRenameClick: (String) -> Unit = {},
) {
    val name = remember { mutableStateOf(currentName) }
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Rename Playlist") },
            text = {
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Playlist Name") },
                    singleLine = true,
                    modifier = Modifier
                )

            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRenameClick(name.value)
                        onDismissRequest()
                    }
                ) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}