package com.chordbay.app.ui.composable.screen.song

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chordbay.app.data.model.util.MainTabs
import com.chordbay.app.ui.composable.component.fab.HomeSortFAB
import com.chordbay.app.ui.composable.component.menu.BottomSheetContent
import com.chordbay.app.ui.composable.component.navdrawer.MyDrawerContent
import com.chordbay.app.ui.composable.component.searchbar.HomeSearchbar
import com.chordbay.app.ui.composable.navigation.Paths
import com.chordbay.app.ui.composable.component.topappbar.HomeTopAppBar
import com.chordbay.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import com.chordbay.app.ui.composable.component.alertdialog.AddSongToPlaylistDialog
import com.chordbay.app.ui.composable.component.alertdialog.CreatePlaylistDialog
import com.chordbay.app.ui.composable.component.alertdialog.DeleteOptionDialog
import com.chordbay.app.ui.composable.component.alertdialog.PrivacyBulkDialog
import com.chordbay.app.ui.composable.component.list.AlphabeticalSongList
import com.chordbay.app.ui.composable.screen.song.RemoteSongsTab
import com.chordbay.app.ui.viewmodel.AuthViewModel
import com.chordbay.app.ui.viewmodel.RemoteSongsViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    remoteSongsViewModel: RemoteSongsViewModel = koinViewModel(),
    navController: NavController,
) {
    val songs = mainViewModel.songs.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val selectedTab = mainViewModel.selectedTab.collectAsState()
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    val sortOption by mainViewModel.sortOption.collectAsState()
    val selectedSongsList by mainViewModel.selectedSongsList.collectAsState()
    val playlists by mainViewModel.playlists.collectAsState()
    var showAddPlaylistDialog by remember { mutableStateOf(false) }
    var showAddSongToPlaylistDialog by remember { mutableStateOf(false) }
    var showPrivacyBulkDialog by remember { mutableStateOf(false) }
    var showDeleteOptionDialog by remember { mutableStateOf(false) }
    val postSuccess = mainViewModel.postSuccess.collectAsState()

    val email = authViewModel.userEmail.collectAsState()
    val isUserLoggedIn = authViewModel.isUserLoggedIn.collectAsState()


    var searchBarExpanded by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchBarIsActive by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
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
                    if (selectedSongsList.isNotEmpty()) 64.dp else 24.dp
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
        selectedSongsList.isNotEmpty(),
//        selectedRemoteSongsList.isNotEmpty(),
        scaffoldState.bottomSheetState.currentValue
    ) {
        derivedStateOf {
            if (selectedTab.value == MainTabs.MY_SONGS) {
                if (selectedSongsList.isNotEmpty()) {
                    BottomSheetDefaults.SheetPeekHeight
                } else {
                    0.dp
                }
            } else {
//                if (selectedRemoteSongsList.isNotEmpty()) {
//                    BottomSheetDefaults.SheetPeekHeight
//                } else {
                    0.dp
//                }
            }
        }
    }
    val context = LocalContext.current
    val error = mainViewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val hbFormatState = mainViewModel.hbFormat.collectAsState()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        mainViewModel.importTxtSongsFromUris(context = context, uris = uris, hbFormat = hbFormatState.value)
    }




    LaunchedEffect(searchQuery) {
        mainViewModel.setSearchQuery(searchQuery)
    }
    LaunchedEffect(email.value) {
    }
    LaunchedEffect(error.value) {
        error.value?.let { errMsg ->
            snackbarHostState.showSnackbar(errMsg)
            mainViewModel.clearError()
        }
    }
    LaunchedEffect(postSuccess.value) {
        if (postSuccess.value == true) {
            snackbarHostState.showSnackbar("Successful upload")
            mainViewModel.clearPostSuccess()
        }
    }

    BackHandler(enabled = searchBarIsActive) {
        searchBarIsActive = false
        keyboardController?.hide()
    }
    BackHandler(enabled = searchBarExpanded && !searchBarIsActive) {
        searchBarExpanded = false
        searchQuery = ""
        keyboardController?.hide()
    }

    ModalNavigationDrawer(
        drawerContent = {
            MyDrawerContent(
                playlists = playlists,
                onPlaylistClick = {
                    navController.navigate(Paths.PlaylistPath.createRoute(it.id))
                },
                onSettingsClick = {
                    navController.navigate(Paths.SettingsPath.route)
                },
                userEmail = email.value,
                onLoginClick = {
                    navController.navigate(Paths.LoginPath.route)
                },
                onManageAccountClick = {
                    navController.navigate(Paths.ManageAccountPath.route)
                },
                onSignOutClick = { authViewModel.logoutUser() },
                onCreatePlaylistClick = {
                    showAddPlaylistDialog = true
                },
                onHelpAndFeedbackClick = {
                    navController.navigate(Paths.HelpPath.route)
                },
                onLegalClick = {
                    navController.navigate(Paths.LegalPath.route)
                },
                onAboutClick = {
                    navController.navigate(Paths.AboutPath.route)
                }
            )
        },
        drawerState = drawerState
    ) {
        BottomSheetScaffold(
            sheetPeekHeight = sheetPeekHeight,
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            sheetContent = {
                when (selectedTab.value) {
                    MainTabs.MY_SONGS -> {
                        BottomSheetContent(
                            bottomPadding = bottomSystemPadding.calculateBottomPadding(),
                            selectedSongs = selectedSongsList,
                            onPostClick = {
                                showPrivacyBulkDialog = true
                            },
                            onEditClick = {
                                if (selectedSongsList.size == 1) {
                                    val song = selectedSongsList[0]
                                    Log.d("HomeScreen", "Editing song ${song.localId}")
                                    navController.navigate(
                                        route = Paths.EditSongPath.createRoute(songId = song.localId.toString())
                                    )
                                    scope.launch {
                                        scaffoldState.bottomSheetState.hide()
                                        mainViewModel.clearSelectedSongs()
                                    }
                                }
                            },
                            onDeleteClick = {
                                showDeleteOptionDialog = true
                            },
                            onAddToPlaylistClick = {
                                scope.launch {
                                    showAddSongToPlaylistDialog = true
                                    scaffoldState.bottomSheetState.hide()
                                }
                            },
                            onCloseClick = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.hide()
                                    mainViewModel.clearSelectedSongs()
                                }
                            }
                        )
                    }

                    MainTabs.REMOTE_SONGS -> {
//                        BottomSheetContentRemote(
//                            selectedRemoteSongs = selectedRemoteSongsList,
//                            onSaveClick = {
//                                songViewModel.saveSelectedRemoteSongsToDatabase()
//                                scope.launch {
//                                    scaffoldState.bottomSheetState.hide()
//                                    songViewModel.clearSelectedRemoteSongs()
//                                }
//                            }
//                        )
                    }
                }
            },
            topBar = {
                HomeTopAppBar(
                    searchBarExpanded = searchBarExpanded,
                    selectedTab = selectedTab.value,
                    onNavigationIconClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onSearchClick = {
                        searchBarExpanded = !searchBarExpanded
                    },
                    onAddClick = {
                        scope.launch {
                            navController.navigate(
                                route = Paths.EditSongPath.createRoute(songId = "new")
                            )
                        }
                    },
                    onMenuClick = {
                        showOptionsMenu = !showOptionsMenu
                    },
                    showOptionsMenu = showOptionsMenu,
                    onMenuToggle = { showOptionsMenu = !showOptionsMenu },
                    onPlaylistClick = {
                        showOptionsMenu = false
                        showAddPlaylistDialog = true
                    },
                    onSyncClick = {
                        showOptionsMenu = false
                        scope.launch {
                            Log.d("HomeScreen", "Syncing songs...")
                            mainViewModel.fetchMyRemoteSongs()
                        }
                    },
                    onImportTxtClick = {
                        importLauncher.launch(arrayOf("text/plain"))
                        showOptionsMenu = false
                    }
                )
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = modifier
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding
                                .calculateStartPadding(LocalLayoutDirection.current),
                            end = innerPadding
                                .calculateEndPadding(LocalLayoutDirection.current),
                        )
                        .fillMaxSize()
                ) {
                    PrimaryTabRow(
                        modifier = Modifier.fillMaxWidth(),
                        selectedTabIndex = selectedTab.value.index,
                    ) {
                        Tab(
                            onClick = {
                                mainViewModel.selectTab(MainTabs.MY_SONGS)
                            },
                            selected = MainTabs.MY_SONGS.index == selectedTab.value.index,
                            text = {
                                Text(MainTabs.MY_SONGS.title)
                            }
                        )
                        Tab(
                            onClick = {
                                mainViewModel.selectTab(MainTabs.REMOTE_SONGS)
                            },
                            selected = MainTabs.REMOTE_SONGS.index == selectedTab.value.index,
                            text = {
                                Text(MainTabs.REMOTE_SONGS.title)
                            }
                        )
                    }
                    if (selectedTab.value == MainTabs.MY_SONGS) {
                        HomeSearchbar(
                            modifier = Modifier.fillMaxWidth(),
                            searchBarExpanded = searchBarExpanded,
                            searchQuery = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = {
                                keyboardController?.hide()
                                searchQuery = it
                            },
                            onSearchClick = {
//                                searchBarExpanded = false
                            },
                            onClearClick = {
                                searchQuery = ""
                            },
                        )
                    }
                    AnimatedContent(
                        targetState = selectedTab.value,
                        transitionSpec = {
                            val direction = if (targetState.index > initialState.index) 1 else -1
                            val enter = slideInHorizontally(
                                initialOffsetX = { fullWidth -> direction * fullWidth },
                                animationSpec = tween(200)
                            ) + fadeIn(animationSpec = tween(100))
                            val exit = slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -direction * fullWidth },
                                animationSpec = tween(200)
                            ) + fadeOut(animationSpec = tween(100))
                            enter.togetherWith(exit)
                        }
                    ) { tab ->
                        when (tab) {
                            MainTabs.MY_SONGS -> {
                                AlphabeticalSongList(
                                    songs = songs.value,
                                    bottomPadding = dynamicBottomPadding,
                                    sortBy = sortOption, // pass current sort option
                                    onSongClick = { song ->
                                        if (selectedSongsList.isNotEmpty()) {
                                            scope.launch {
                                                mainViewModel.selectSong(song)
                                            }
                                        } else {
                                            navController.navigate(
                                                Paths.SongPath.createRoute(
                                                    song.localId.toString()
                                                )
                                            )
                                        }
                                    },
                                    onSongLongClick = { song ->
                                        scope.launch {
                                            mainViewModel.selectSong(song)
                                            scaffoldState.bottomSheetState.expand()
                                        }
                                    },
                                    selectedSongs = selectedSongsList,
                                    searchQuery = searchQuery
                                )
                            }
//-------------------------------- REMOTE SONGS-----------------------------------------------------
                            MainTabs.REMOTE_SONGS -> {
                                RemoteSongsTab(
                                    remoteSongsViewModel = remoteSongsViewModel,
                                    songsViewModel = mainViewModel,
                                    navController = navController,
                                )
                            }
                        }
                    }
                }
                if (selectedTab.value == MainTabs.MY_SONGS) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = dynamicBottomPadding)
                    ) {
                        HomeSortFAB(
                            modifier = Modifier
                                .align(Alignment.BottomEnd),
                            onFabClick = {
                                showFabMenu = !showFabMenu
                            },
                            isFabMenuExpanded = showFabMenu,
                            onMenuToggle = { showFabMenu = !showFabMenu },
                            sortBy = sortOption,
                            onSortSelected = { selected ->
                                mainViewModel.setSortOption(selected)
                            }
                        )
                    }
                }
                // ------------------------ Dialogs ------------------------------------------------
                if (showAddPlaylistDialog) {
                    CreatePlaylistDialog(
                        onDismissRequest = { showAddPlaylistDialog = false },
                        onCreatePlaylist = { playlistName ->
                            mainViewModel.createPlaylist(playlistName)
                            showAddPlaylistDialog = false
                        },
                    )
                }
                if (showAddSongToPlaylistDialog) {
                    AddSongToPlaylistDialog(
                        onDismiss = { showAddSongToPlaylistDialog = false },
                        playlists = playlists,
                        onConfirm = { playlistId ->
                            for (song in selectedSongsList) {
                                Log.d(
                                    "HomeScreen",
                                    "Adding song ${song.title} to playlist $playlistId"
                                )
                                mainViewModel.addSongToPlaylist(
                                    song = song,
                                    playlistId = playlistId
                                )
                            }
                            mainViewModel.clearSelectedSongs()
                            showAddSongToPlaylistDialog = false
                        }
                    )
                }

                if (showPrivacyBulkDialog && selectedSongsList.isNotEmpty()) {
                    PrivacyBulkDialog(
                        songs = selectedSongsList,
                        onDismiss = { showPrivacyBulkDialog = false },
                        onApply = { defaultIsPublic, overrides ->
                            // Apply privacy and post
                            mainViewModel.applyPrivacyAndPost(
                                songs = selectedSongsList,
                                defaultIsPublic = defaultIsPublic,
                                overrides = overrides
                            )
                            showPrivacyBulkDialog = false
                            scope.launch {
                                scaffoldState.bottomSheetState.hide()
                                mainViewModel.clearSelectedSongs()
                            }
                        }
                    )
                }

                if (showDeleteOptionDialog && selectedSongsList.isNotEmpty()) {
                    DeleteOptionDialog(
                        songs = selectedSongsList,
                        onDismiss = { showDeleteOptionDialog = false },
                        onDelete = { deleteAction ->
                            // Delete songs based on deleteAction
                            mainViewModel.deleteSongWithOptions(
                                songs = selectedSongsList,
                                deleteAction = deleteAction
                            )
                            showDeleteOptionDialog = false
                            scope.launch {
                                scaffoldState.bottomSheetState.hide()
                                mainViewModel.clearSelectedSongs()
                            }
                        }
                    )
                }
            }
        }
    }
}

