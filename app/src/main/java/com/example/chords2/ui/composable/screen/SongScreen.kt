package com.example.chords2.ui.composable.screen

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
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chords2.data.helper.TxtSongIO
import com.example.chords2.data.helper.calculatePercentage
import com.example.chords2.data.helper.findKey
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.util.HBFormat
import com.example.chords2.ui.composable.component.text.SongText
import com.example.chords2.ui.composable.component.button.TransposeButton
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.MainViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SongScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel(),
    songId: String,
    navController: NavController,
    isRemote: Boolean = false,
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

    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        mainViewModel.exportSongAsTxt(uri, song, context)
    }

    val isDropdownMenuExpanded = remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = song?.artist ?: "",
                subtitle = song?.title ?: "",
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
                        val key = findKey(song.content, hbFormat = hbFormat.value, songHBFormat = song.hBFormat)
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
                    Box() {
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
                                text = { Text("Change Font Size") },
                                onClick = { showSlider = true },
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Song") },
                                onClick = {
                                    if (song?.localId != null) {
                                        navController.navigate(
                                            Paths.EditSongPath.createRoute(songId = song.localId.toString())
                                        )
                                    }
                                }
                            )
                            if (song != null) {
                                DropdownMenuItem(
                                    text = { Text("Export as TXT") },
                                    onClick = {
                                        val suggestedFileName = TxtSongIO.buildFileName(song)
                                        exportLauncher.launch(suggestedFileName)
                                    }
                                )
                            }
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
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            if (zoom != 1f) {
                                sliderState.floatValue = (sliderState.floatValue * zoom).coerceIn(10f, 30f)
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
                            .verticalScroll(rememberScrollState())
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
            AnimatedVisibility(
                visible = showSlider,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
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
                        valueRange = 10f..30f,
                        steps = 19,
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
                                    text = calculatePercentage(10..30, sliderState.floatValue)
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
    }
}