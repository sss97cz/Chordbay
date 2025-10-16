package com.example.chords2.ui.composable.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chords2.data.mappers.toSongUi
import com.example.chords2.data.model.util.MainTabs
import com.example.chords2.ui.composable.component.fab.HomeSortFAB
import com.example.chords2.ui.composable.component.listitem.RemoteSongItem
import com.example.chords2.ui.composable.component.menu.BottomSheetContent
import com.example.chords2.ui.composable.component.navdrawer.MyDrawerContent
import com.example.chords2.ui.composable.component.searchbar.HomeSearchbar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.composable.component.topappbar.HomeTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.draw.clip
import com.example.chords2.ui.composable.component.alertdialog.AddSongToPlaylistDialog
import com.example.chords2.ui.composable.component.alertdialog.CreatePlaylistDialog
import com.example.chords2.ui.composable.component.list.AlphabeticalSongList
import com.example.chords2.ui.composable.component.listitem.ArtistItem
import com.example.chords2.ui.composable.component.menu.BottomSheetContentRemote
import com.example.chords2.ui.viewmodel.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    navController: NavController,
) {
    val songs = songViewModel.songs.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val selectedTab = songViewModel.selectedTab.collectAsState()
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    val sortOption by songViewModel.sortOption.collectAsState()
    val selectedSongsList by songViewModel.selectedSongsList.collectAsState()
    val selectedRemoteSongsList by songViewModel.selectedRemoteSongs.collectAsState()
    val playlists by songViewModel.playlists.collectAsState()
    var showAddPlaylistDialog by remember { mutableStateOf(false) }
    var showAddSongToPlaylistDialog by remember { mutableStateOf(false) }

    val email = authViewModel.userEmail.collectAsState()


    var searchBarExpanded by remember { mutableStateOf(false) }
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
        selectedRemoteSongsList.isNotEmpty(),
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
                if (selectedRemoteSongsList.isNotEmpty()) {
                    BottomSheetDefaults.SheetPeekHeight
                } else {
                    0.dp
                }
            }
        }
    }


    LaunchedEffect(searchQuery) {
        songViewModel.setSearchQuery(searchQuery)
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
                onSignOutClick = { authViewModel.logoutUser() }
            )
        },
        drawerState = drawerState
    ) {
        BottomSheetScaffold(
            sheetPeekHeight = sheetPeekHeight,
            scaffoldState = scaffoldState,
            sheetContent = {
                when (selectedTab.value) {
                    MainTabs.MY_SONGS -> {
                        BottomSheetContent(
                            bottomPadding = bottomSystemPadding.calculateBottomPadding(),
                            selectedSongs = selectedSongsList,
                            onPostClick = {
                                songViewModel.postSongs(selectedSongsList)
                                scope.launch {
                                    scaffoldState.bottomSheetState.hide()
                                    songViewModel.clearSelectedSongs()
                                }
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
                                        songViewModel.clearSelectedSongs()
                                    }
                                }
                            },
                            onDeleteClick = {
                                for (song in selectedSongsList) {
                                    songViewModel.deleteSong(song)
                                }
                                if (scaffoldState.bottomSheetState.currentValue != SheetValue.Hidden) {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.hide()
                                        songViewModel.clearSelectedSongs()
                                    }
                                }
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
                                    songViewModel.clearSelectedSongs()
                                }
                            }
                        )
                    }

                    MainTabs.REMOTE_SONGS -> {
                        BottomSheetContentRemote(
                            selectedRemoteSongs = selectedRemoteSongsList,
                            onSaveClick = {
                                songViewModel.saveSelectedRemoteSongsToDatabase()
                                scope.launch {
                                    scaffoldState.bottomSheetState.hide()
                                    songViewModel.clearSelectedRemoteSongs()
                                }
                            }
                        )
                    }
                }
            },
            topBar = {
                HomeTopAppBar(
                    title = selectedTab.value.title,
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
                                songViewModel.selectTab(MainTabs.MY_SONGS)
                            },
                            selected = MainTabs.MY_SONGS.index == selectedTab.value.index,
                            text = {
                                Text("My Songs")
                            }
                        )
                        Tab(
                            onClick = {
                                songViewModel.selectTab(MainTabs.REMOTE_SONGS)
                            },
                            selected = MainTabs.REMOTE_SONGS.index == selectedTab.value.index,
                            text = {
                                Text("Remote Songs")
                            }
                        )
                    }
                    if (searchBarExpanded) {
                        HomeSearchbar(
                            searchBarExpanded = searchBarExpanded,
                            searchQuery = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = {
                                keyboardController?.hide()
                                searchQuery = it
                                searchBarExpanded = false
                            },
                            onSearchClick = {
                                searchBarExpanded = false
                            },
                            onClearClick = {
                                searchQuery = ""
                            }
                        )
                    } else if (searchQuery.isNotEmpty()) {
                        Text(": \"$searchQuery\"", fontSize = 20.sp)
                    }
                    when (selectedTab.value) {
//-------------------------------- MY SONGS---------------------------------------------------------
                        MainTabs.MY_SONGS -> {
                            AlphabeticalSongList(
                                songs = songs.value,
                                bottomPadding = dynamicBottomPadding,
                                sortBy = sortOption, // pass current sort option
                                onSongClick = { song ->
                                    if (selectedSongsList.isNotEmpty()) {
                                        scope.launch {
                                            songViewModel.selectSong(song)
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
                                        songViewModel.selectSong(song)
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                },
                                selectedSongs = selectedSongsList
                            )
                        }
//-------------------------------- REMOTE SONGS-----------------------------------------------------
                        MainTabs.REMOTE_SONGS -> {
                            val artists = songViewModel.artists.collectAsState()
                            LaunchedEffect(Unit) {
                                songViewModel.fetchAllArtists()
                                Log.d("HomeScreen", "Fetched artists: ${artists.value}")
                            }
                            val isRefreshing = songViewModel.isLoading.collectAsState()
                            val pullToRefreshState = rememberPullToRefreshState()


                            PullToRefreshBox(
                                modifier = Modifier.fillMaxSize(),
                                isRefreshing = isRefreshing.value,
                                onRefresh = {
                                    scope.launch {
                                        songViewModel.fetchAllArtists()
                                    }
                                },
                                state = pullToRefreshState,
                                indicator = {
                                    PullToRefreshDefaults.Indicator(
                                        state = pullToRefreshState,
                                        modifier = Modifier.align(Alignment.TopCenter),
                                        isRefreshing = isRefreshing.value
                                    )
                                }
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(
                                        top = 4.dp,
                                        bottom = dynamicBottomPadding
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    items(artists.value) { artist ->
                                        ArtistItem(
                                            artist = artist.name,
                                            songCount = artist.songCount,
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                navController.navigate(
                                                    Paths.ArtistSongsPath.createRoute(artist.name)
                                                )
                                            }
                                        )
                                    }
                                }
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
                                songViewModel.setSortOption(selected)
                            }
                        )
                    }
                }
                // ------------------------ Dialogs ------------------------------------------------
                if (showAddPlaylistDialog) {
                    CreatePlaylistDialog(
                        onDismissRequest = { showAddPlaylistDialog = false },
                        onCreatePlaylist = { playlistName ->
                            songViewModel.createPlaylist(playlistName)
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
                                songViewModel.addSongToPlaylist(
                                    song = song,
                                    playlistId = playlistId
                                )
                            }
                            songViewModel.clearSelectedSongs()
                            showAddSongToPlaylistDialog = false
                        }
                    )
                }
            }
        }
    }
}

