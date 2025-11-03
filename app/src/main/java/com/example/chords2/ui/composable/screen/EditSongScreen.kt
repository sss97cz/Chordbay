package com.example.chords2.ui.composable.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.Song
import com.example.chords2.ui.composable.component.alertdialog.EditInfoAlertDialog
import com.example.chords2.ui.composable.component.textfield.SongContentEditor
import com.example.chords2.ui.composable.component.textfield.SongTextField
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditSongScreen(
    modifier: Modifier = Modifier,
    songId: String,
    navController: NavController,
    songViewModel: SongViewModel = koinViewModel(),
) {
    val songName by songViewModel.songName.collectAsState()
    val songArtist by songViewModel.songArtist.collectAsState()
    val songContent by songViewModel.songContent.collectAsState()
    val hasLoaded by songViewModel.hasLoadedEdit.collectAsState()
    val showInfoAlertDialog = rememberSaveable { mutableStateOf(false) }

    // Load song only once per songId
    LaunchedEffect(songId) {
        if (!hasLoaded) {
            songViewModel.loadEditSong(songId)
        }
    }
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route.orEmpty()
            if (!route.contains("editSong/$songId")) {
                songViewModel.clearSongStates()
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
            songViewModel.clearSongStates()
        }
    }
    val canNavigateBack = navController.previousBackStackEntry != null
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Song Editor",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                navigationIconContentDescription = if (canNavigateBack) "Back" else null,
                onNavigationIconClick = if (canNavigateBack) {
                    {
                        navController.navigateUp()
                        songViewModel.clearSongStates()
                    }
                } else null, actions = {
                    IconButton(onClick = {
                        showInfoAlertDialog.value = true
                    }) {
                        Icon(Icons.Outlined.Info, contentDescription = "Cancel")
                    }
                    IconButton(onClick = {
                        songViewModel.saveEditedSong(songId)
                        navController.navigateUp()
                        songViewModel.clearSongStates()
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SongTextField(
                    modifier = Modifier.weight(1f),
                    value = songName ?: "",
                    onValueChange = { songViewModel.setSongName(it) },
                    singleLine = true,
                    label = "Song Title"
                )
                SongTextField(
                    modifier = Modifier.weight(1f),
                    value = songArtist,
                    onValueChange = { songViewModel.setSongArtist(it) },
                    singleLine = true,
                    label = "Artist"
                )
            }
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                SongContentEditor(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    value = songContent,
                    onValueChange = { songViewModel.setSongContent(it) },
                )
            }
            EditInfoAlertDialog(
                showAlertDialog = showInfoAlertDialog.value,
                onDismissRequest = { showInfoAlertDialog.value = false }
            )
        }
    }
}
