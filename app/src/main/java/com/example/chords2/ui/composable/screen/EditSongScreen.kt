package com.example.chords2.ui.composable.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.navigation.NavController
import com.example.chords2.ui.composable.component.alertdialog.EditInfoAlertDialog
import com.example.chords2.ui.composable.component.textfield.SongEditor
import com.example.chords2.ui.composable.component.textfield.SongTextField
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.EditViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditSongScreen(
    modifier: Modifier = Modifier,
    songId: String,
    navController: NavController,
    viewModel: EditViewModel,
) {
    //TODO(Add an dialog for saving empty song)
    val songName by viewModel.songName.collectAsState()
    val songArtist by viewModel.songArtist.collectAsState()
    val songContent by viewModel.songContent.collectAsState()
    val hasLoaded by viewModel.hasLoadedEdit.collectAsState()
    val showInfoAlertDialog = rememberSaveable { mutableStateOf(false) }

    // Load song only once per songId
    LaunchedEffect(songId) {
        if (!hasLoaded) {
            viewModel.loadEditSong(songId)
        }
    }
    DisposableEffect(navController, songId) {
        val listener = NavController.OnDestinationChangedListener { _, destination, args ->
            val route = destination.route.orEmpty()
            val templatedRoute = "editSong/{songId}"
            val destSongId = args?.getString("songId")

            // Only clear when we really navigated away from this edit screen for this songId
            val stillOnSameEditRoute =
                route == "editSong/$songId" || route == templatedRoute || destSongId == songId
            if (!stillOnSameEditRoute) {
                viewModel.clearSongStates()
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    val canNavigateBack = navController.previousBackStackEntry != null
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    Scaffold(
        topBar = {
            MyTopAppBar(

                title = "Song Editor",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                navigationIconContentDescription = if (canNavigateBack) "Back" else null,
                onNavigationIconClick = if (canNavigateBack) {
                    {
                        navController.navigateUp()
                        viewModel.clearSongStates()
                    }
                } else null, actions = {
                    IconButton(onClick = {
                        showInfoAlertDialog.value = true
                    }) {
                        Icon(Icons.Outlined.Info, contentDescription = "Cancel")
                    }
                    IconButton(onClick = {
                        viewModel.saveEditedSong(songId)
                        navController.navigateUp()
                        viewModel.clearSongStates()
                    }) {
                        Icon(Icons.Filled.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLandscape) {
            // inside your @Composable where you have `innerPadding`:
            val layoutDirection = LocalLayoutDirection.current
            val navPadding = WindowInsets.navigationBars.asPaddingValues()

            val start = max(0.dp, innerPadding.calculateStartPadding(layoutDirection) - navPadding.calculateStartPadding(layoutDirection))
            val end = max(0.dp, innerPadding.calculateEndPadding(layoutDirection) - navPadding.calculateEndPadding(layoutDirection))
            val top = max(0.dp, innerPadding.calculateTopPadding() - navPadding.calculateTopPadding())
            val bottom = max(0.dp, innerPadding.calculateBottomPadding() - navPadding.calculateBottomPadding())

            val adjustedInnerPadding = PaddingValues(start = start, top = top, end = end, bottom = bottom)
            val imeVisible = WindowInsets.isImeVisible
            Row(
                modifier = modifier
                    .padding(if (imeVisible) adjustedInnerPadding else innerPadding)
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .imePadding(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = CardDefaults.outlinedCardBorder(true)
                ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SongTextField(
                        value = songName ?: "",
                        onValueChange = { viewModel.setSongName(it) },
                        singleLine = true,
                        label = "Song Title"
                    )
                    SongTextField(
                        value = songArtist,
                        onValueChange = { viewModel.setSongArtist(it) },
                        singleLine = true,
                        label = "Artist"
                    )
                }
              }
                SongEditor(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth(),
                    value = songContent,
                    onValueChange = { viewModel.setSongContent(it) },
                )
            }
        } else {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp)
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SongTextField(
                        modifier = Modifier.weight(1f),
                        value = songName ?: "",
                        onValueChange = { viewModel.setSongName(it) },
                        singleLine = true,
                        label = "Song Title"
                    )
                    SongTextField(
                        modifier = Modifier.weight(1f),
                        value = songArtist,
                        onValueChange = { viewModel.setSongArtist(it) },
                        singleLine = true,
                        label = "Artist"
                    )
                }
                SongEditor(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .weight(1f),
                    value = songContent,
                    onValueChange = { viewModel.setSongContent(it) },
                )

                EditInfoAlertDialog(
                    showAlertDialog = showInfoAlertDialog.value,
                    onDismissRequest = { showInfoAlertDialog.value = false }
                )
            }
        }
    }
}
