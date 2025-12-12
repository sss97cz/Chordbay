package com.chordbay.app.ui.composable.screen.song

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayForWork
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chordbay.app.data.helper.TxtSongIO
import com.chordbay.app.data.helper.calculatePercentage
import com.chordbay.app.data.helper.findKey
import com.chordbay.app.data.helper.openExternalApp
import com.chordbay.app.data.model.Song
import com.chordbay.app.ui.composable.component.button.TransposeButton
import com.chordbay.app.ui.composable.component.text.SongText
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar
import com.chordbay.app.ui.composable.navigation.Paths
import com.chordbay.app.ui.viewmodel.MainViewModel
import com.chordbay.app.ui.viewmodel.RemoteSongsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel(),
    remoteSongsViewModel: RemoteSongsViewModel = koinViewModel(),
    songId: String,
    navController: NavController,
    isRemote: Boolean = false,
    fromPlaylistId: Int? = null,
) {
    val songData by produceState<Song?>(initialValue = null, key1 = songId) {
        value = if (!isRemote && songId.toIntOrNull() != null) {
            mainViewModel.getSongById(songId.toInt()).collect { songValue ->
                value = songValue
            }
        } else if (isRemote) {
            mainViewModel.getRemoteSongById(songId)
            mainViewModel.remoteSongById.collect { value = it }
        } else {
            null
        }
    }
    val song = songData
    val canNavigateBack = remember { navController.previousBackStackEntry != null }
    var semitones by rememberSaveable { mutableIntStateOf(0) }
    val fontSize = mainViewModel.songTextFontSize.collectAsState()
    val sliderState = remember { mutableFloatStateOf(fontSize.value.toFloat()) }
    var showSlider by rememberSaveable { mutableStateOf(false) }
    val hbFormat = mainViewModel.hbFormat.collectAsState()
    val error = remoteSongsViewModel.error.collectAsState()

    var showPlayExternalDialog by rememberSaveable { mutableStateOf(false) }

    var showAutoscrollFab by rememberSaveable { mutableStateOf(false) }
    val isAutoscrollMenuVisible = rememberSaveable { mutableStateOf(false) }

    var autoScrollSpeed by rememberSaveable { mutableFloatStateOf(15f) }
    var isAutoScrollEnabled by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        mainViewModel.exportSongAsTxt(uri, song, context)
    }

    val isDropdownMenuExpanded = remember { mutableStateOf(false) }
    val sliderBottomPadding = if (showSlider) 130.dp else 0.dp
    val playlistNavPadding = if (fromPlaylistId != null) 48.dp else 0.dp

    val sliderInteraction = remember { MutableInteractionSource() }
    val sliderColors = SliderDefaults.colors(
        thumbColor = MaterialTheme.colorScheme.primary,
        activeTrackColor = MaterialTheme.colorScheme.primary,
        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
    )
    LaunchedEffect(mainViewModel) {
        snapshotFlow { sliderState.floatValue }
            .debounce(300) // wait 300ms after last change
            .distinctUntilChanged()
            .collect { value ->
                mainViewModel.setSongTextFontSize(value.toInt())
            }
    }
    LaunchedEffect(song) {
        Log.d("SongScreen", "Loaded song: $song")
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val saveSuccess = remoteSongsViewModel.saveSuccess.collectAsState()
    LaunchedEffect(saveSuccess.value) {
        if (saveSuccess.value == true) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    "Song Downloaded"
                )
                remoteSongsViewModel.clearSaveSuccess()
            }
        }
    }
    LaunchedEffect(error.value) {
        if (error.value != null && isRemote) {
            scope.launch {
                snackbarHostState.showSnackbar(error.value ?: "")
                remoteSongsViewModel.clearError()
            }
        }
    }
    val scrollState = rememberScrollState()
    LaunchedEffect(isAutoScrollEnabled, autoScrollSpeed, song) {
        if (!isAutoScrollEnabled || song == null || autoScrollSpeed <= 0f) return@LaunchedEffect

        val frameMs = 16L
        while (isAutoScrollEnabled && autoScrollSpeed > 0f && isActive) {
            val deltaPx = autoScrollSpeed * (frameMs / 1000f)
            val remaining = (scrollState.maxValue - scrollState.value).toFloat()
            val toScroll = deltaPx.coerceAtMost(remaining)

            if (toScroll <= 0f) break
            scrollState.scrollBy(toScroll)

            if (scrollState.value >= scrollState.maxValue) break
            delay(frameMs)
        }
    }
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isUserScrolling ->
                if (isUserScrolling && isAutoScrollEnabled) {
                    isAutoScrollEnabled = false
                }
            }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MyTopAppBar(
                title = song?.title ?: "",
                subtitle = song?.artist ?: "",
                navigationIcon = if (canNavigateBack) {
                    Icons.AutoMirrored.Filled.ArrowBack
                } else null,
                onNavigationIconClick = {
                    if (canNavigateBack) {
                        if (fromPlaylistId != null) {
                            // Clear all songs from this playlist from the back stack
                            navController.navigate(
                                Paths.PlaylistPath.createRoute(fromPlaylistId)
                            ) {
                                popUpTo(Paths.PlaylistPath.route) {
                                    inclusive = true   // remove old playlist + its song entries
                                }
                                launchSingleTop = true
                            }
                        } else {
                            remoteSongsViewModel.clearSaveSuccess()
                            navController.navigateUp()
                        }
                    }
                },
                actions = {
                    if (song != null) {
                        val key = findKey(
                            song.content,
                            hbFormat = hbFormat.value,
                            songHBFormat = song.hBFormat
                        )
                        if (key != null) {
                            TransposeButton(
                                initialSemitones = semitones,
                                initialChord = key,
                                onUpClick = {
                                    semitones += 1
                                },
                                onDownClick = {
                                    semitones -= 1
                                },
                                hBFormat = hbFormat.value,
                                songHBFormat = song.hBFormat
                            )
                        }
                    }
                    Box {
                        IconButton(
                            onClick = {
                                isDropdownMenuExpanded.value = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }
                        DropdownMenu(
                            expanded = isDropdownMenuExpanded.value,
                            onDismissRequest = { isDropdownMenuExpanded.value = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Font Size") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.FormatSize,
                                        contentDescription = "Font Size"
                                    )
                                },
                                onClick = { showSlider = true },
                            )
                            DropdownMenuItem(
                                text = { Text("Show Autoscroll") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.PlayForWork,
                                        contentDescription = "Autoscroll"
                                    )
                                },
                                onClick = {
                                    showAutoscrollFab = !showAutoscrollFab
                                },
                            )
                            if (!isRemote) {
                                DropdownMenuItem(
                                    text = { Text("Edit Song") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit Song"
                                        )
                                    },
                                    onClick = {
                                        if (song?.localId != null) {
                                            Log.d("SongScreen", "Navigating to EditSongPath for song ID ${song.localId}")
                                            navController.navigate(
                                                Paths.EditSongPath.createRoute(songId = song.localId.toString())
                                            )
                                        }
                                    }
                                )
                                if (song != null) {
                                    DropdownMenuItem(
                                        text = { Text("Export as TXT") },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Filled.Output,
                                                contentDescription = "Export as TXT"
                                            )
                                        },
                                        onClick = {
                                            val suggestedFileName = TxtSongIO.buildFileName(song)
                                            exportLauncher.launch(suggestedFileName)
                                        }
                                    )
                                }
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Download Song") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Download,
                                            contentDescription = "Download Song"
                                        )
                                    },
                                    onClick = {
                                        if (song != null) {
                                            remoteSongsViewModel.saveSong(song)
                                        }
                                    }
                                )
                            }
                            if (song != null) {
                                DropdownMenuItem(
                                    text = { Text("Play on") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = "Play External"
                                        )
                                    },
                                    onClick = {
                                        isDropdownMenuExpanded.value = false
                                        showPlayExternalDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if (song != null && showAutoscrollFab) {
                AutoscrollFab(
                    modifier = Modifier.padding(
                        bottom = sliderBottomPadding + playlistNavPadding
                    ),
                    isAutoScrollEnabled = isAutoScrollEnabled,
                    autoScrollSpeed = autoScrollSpeed,
                    onFabClick = {
                        isAutoscrollMenuVisible.value = !isAutoscrollMenuVisible.value
                    },
                    onPlayClick = {
                        isAutoScrollEnabled = !isAutoScrollEnabled
                    }
                )
            }
        }
    ) { innerPanning ->
        Box(
            modifier = Modifier
                .padding(innerPanning)
                .fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                if (zoom != 1f) {
                                    sliderState.floatValue =
                                        (sliderState.floatValue * zoom).coerceIn(8f, 30f)
                                }
                            }
                        }
                        .pointerInput(fromPlaylistId, song?.localId) {
                            detectDragGestures { change, dragAmount ->
                                if (fromPlaylistId != null && song != null && song.localId != null) {
                                    change.consume()
                                    val threshold = 50f
                                    if (dragAmount.x > threshold) {
                                        // dragged to right
                                        if (mainViewModel.isNotFirstSongInPlaylist(
                                                songId = song.localId,
                                                playlistId = fromPlaylistId,
                                            )
                                        ) {
                                            mainViewModel.navigateInsidePlaylist(
                                                navController = navController,
                                                currentSongId = song.localId,
                                                playlistId = fromPlaylistId,
                                                direction = -1
                                            )
                                        }
                                    } else if (dragAmount.x < -threshold) {
                                        // dragged to left
                                        if (mainViewModel.isNotLastSongInPlaylist(
                                                songId = song.localId,
                                                playlistId = fromPlaylistId,
                                            )
                                        ) {
                                            mainViewModel.navigateInsidePlaylist(
                                                navController = navController,
                                                currentSongId = song.localId,
                                                playlistId = fromPlaylistId,
                                                direction = 1
                                            )
                                        }
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (song == null) {
                        Log.d("SongScreen", "Song with ID $songId not found.")
                        CircularProgressIndicator()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(8.dp)
                        ) {
                            Log.d("SongScreen", "song.hBFormat: ${song.hBFormat}")
                            SongText(
                                modifier = Modifier.fillMaxSize(),
                                text = song.content,
                                semitones = semitones,
                                chordsColor = MaterialTheme.colorScheme.primary,
                                fontSize = sliderState.floatValue.toInt(),
                                hBFormat = hbFormat.value,
                                hbFormatSong = song.hBFormat,
                            )
                        }
                    }
                }
                if (fromPlaylistId != null && song != null && song.localId != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .padding(horizontal = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            PlaylistNavigationButton(
                                direction = -1,
                                isVisible = mainViewModel.isNotFirstSongInPlaylist(
                                    songId = song.localId,
                                    playlistId = fromPlaylistId,
                                ),
                                onClick = { direction ->
                                    mainViewModel.navigateInsidePlaylist(
                                        navController = navController,
                                        currentSongId = song.localId,
                                        playlistId = fromPlaylistId,
                                        direction = direction
                                    )
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${
                                        mainViewModel.positionInPlaylist(
                                            songId = song.localId,
                                            playlistId = fromPlaylistId,
                                        )
                                    } / ${mainViewModel.playlistSize(playlistId = fromPlaylistId)}",
                                )
                            }
                            PlaylistNavigationButton(
                                direction = 1,
                                isVisible = mainViewModel.isNotLastSongInPlaylist(
                                    songId = song.localId,
                                    playlistId = fromPlaylistId,
                                ),
                                onClick = { direction ->
                                    mainViewModel.navigateInsidePlaylist(
                                        navController = navController,
                                        currentSongId = song.localId,
                                        playlistId = fromPlaylistId,
                                        direction = direction
                                    )
                                }
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showSlider,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Font Size",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            IconButton(
                                onClick = { showSlider = false },
                                modifier = Modifier
                                    .clip(CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                        Slider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            value = sliderState.floatValue,
                            onValueChange = {
                                sliderState.floatValue = it
                            },
                            valueRange = 8f..30f,
                            steps = 21,
                            onValueChangeFinished = {
                                mainViewModel.setSongTextFontSize(sliderState.floatValue.toInt())
                            },
                            thumb = {
                                Box(
                                    modifier = Modifier.size(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SliderDefaults.Thumb(
                                        interactionSource = sliderInteraction,
                                        colors = sliderColors,
                                        thumbSize = DpSize(26.dp, 26.dp)
                                    )
                                    Text(
                                        text = calculatePercentage(8..30, sliderState.floatValue)
                                            .toString() + "%",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            textAlign = TextAlign.Center
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .offset(y = (-25).dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
            if (isAutoscrollMenuVisible.value && song != null) {
                val sliderBottomPadding = if (showSlider) 130.dp else 0.dp
                val playlistNavPadding = if (fromPlaylistId != null) 48.dp else 0.dp
                AutoscrollSpeedPopup(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = sliderBottomPadding + playlistNavPadding,
                        ),
                    autoScrollSpeed = autoScrollSpeed,
                    isAutoScrollEnabled = isAutoScrollEnabled,
                    onSpeedChange = { newSpeed ->
                        autoScrollSpeed = newSpeed
                    },
                    onToggle = { enabled ->
                        isAutoScrollEnabled = enabled
                        if (!enabled) {
                            autoScrollSpeed = 0f
                        } else if (autoScrollSpeed <= 0f) {
                            autoScrollSpeed = 20f
                        }
                    },
                    onDismiss = { isAutoscrollMenuVisible.value = false }
                )
            }
        }
        if (showPlayExternalDialog && song != null) {
            PlayOnExternalAppDialog(
                songArtist = song.artist,
                songTitle = song.title,
                onDismiss = { showPlayExternalDialog = false },
                context = context
            )
        }
    }
}


@Composable
fun PlaylistNavigationButton(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    onClick: (Int) -> Unit,
    direction: Int
) {
    Surface(
        tonalElevation = if (isVisible) 8.dp else 1.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(bottom = 4.dp)
    ) {
        IconButton(
            onClick = {
                onClick(direction)
            },
            enabled = isVisible,
            modifier = Modifier
                .size(45.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                imageVector = if (direction < 0) Icons.AutoMirrored.Filled.ArrowLeft else Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = "Navigate in Playlist"
            )
        }
    }

}

@Composable
fun AutoscrollFab(
    isAutoScrollEnabled: Boolean,
    autoScrollSpeed: Float,
    onFabClick: () -> Unit,
    onPlayClick: () -> Unit = {},
    modifier: Modifier,
) {
    Surface(
        modifier = modifier,
        tonalElevation = 12.dp,
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onPlayClick,
            ) {
                Icon(
                    imageVector = if (isAutoScrollEnabled) {
                        Icons.Default.Pause
                    } else {
                        Icons.Default.PlayArrow

                    },
                    contentDescription = if (isAutoScrollEnabled) {
                        "Autoscroll running"
                    } else {
                        "Autoscroll stopped"
                    }
                )
            }
            VerticalDivider(modifier = Modifier.height(24.dp))
            IconButton(
                onClick = onFabClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Settings, null
                )
            }
        }
    }
}

@Composable
fun AutoscrollSpeedPopup(
    modifier: Modifier = Modifier,
    autoScrollSpeed: Float,
    isAutoScrollEnabled: Boolean,
    onSpeedChange: (Float) -> Unit,
    onToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val minSpeed = 0f
    val maxSpeed = 80f

    // This Box is expected to be inside a full-screen Box in SongScreen
    Box(
        modifier = modifier
            .padding(end = 16.dp, bottom = 88.dp), // 16dp margin + ~72dp FAB height
    ) {
        Surface(
            tonalElevation = 6.dp,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .widthIn(min = 220.dp, max = 320.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Autoscroll",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close autoscroll controls"
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Speed",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.widthIn(min = 56.dp)
                    )
                    Slider(
                        value = autoScrollSpeed.coerceIn(minSpeed, maxSpeed),
                        onValueChange = { value ->
                            onSpeedChange(value)
                        },
                        valueRange = minSpeed..maxSpeed,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.widthIn(min = 30.dp),
                        text = "${autoScrollSpeed.toInt()} px/s",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PlayOnExternalAppDialog(
    songArtist: String,
    songTitle: String,
    onDismiss: () -> Unit,
    context: android.content.Context
) {
    val query = "$songArtist $songTitle"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Play song on") },
        text = {
            Column {
                PlayOptionRow(
                    text = "Spotify",
                    icon = Icons.Default.MusicNote,
                    onClick = { openExternalApp(context, "Spotify", query); onDismiss() }
                )
                PlayOptionRow(
                    text = "YouTube",
                    icon = Icons.Default.SlowMotionVideo,
                    onClick = { openExternalApp(context, "YouTube", query); onDismiss() }
                )
                PlayOptionRow(
                    text = "YouTube Music",
                    icon = Icons.Default.LibraryMusic,
                    onClick = { openExternalApp(context, "YouTube Music", query); onDismiss() }
                )
                PlayOptionRow(
                    text = "Browser",
                    icon = Icons.Default.Public,
                    onClick = { openExternalApp(context, "Browser", query); onDismiss() }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PlayOptionRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
