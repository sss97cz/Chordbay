package com.example.chords2.ui.composable.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chords2.data.mappers.toSongUi
import com.example.chords2.data.model.util.MainTabs
import com.example.chords2.ui.composable.component.fab.HomeSortFAB
import com.example.chords2.ui.composable.component.listitem.RemoteSongItem
import com.example.chords2.ui.composable.component.listitem.SongItem
import com.example.chords2.ui.composable.component.navdrawer.MyDrawerContent
import com.example.chords2.ui.composable.component.searchbar.HomeSearchbar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.composable.topappbar.HomeTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel = koinViewModel(),
    navController: NavController,
    // setTopAppBarConfig: (String, @Composable RowScope.() -> Unit) -> Unit
) {
    val songs = songViewModel.songs.collectAsState()
    val scope = rememberCoroutineScope()
    var enableEditing by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val selectedTab = songViewModel.selectedTab.collectAsState()
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    val sortOption by songViewModel.sortOption.collectAsState()


    var searchBarExpanded by remember { mutableStateOf(false) } // YOUR EXISTING STATE TO CONTROL VISIBILITY
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchBarIsActive by rememberSaveable { mutableStateOf(false) } // For SearchBar's own active state (results overlay)
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }


    // Update ViewModel from searchQuery
    LaunchedEffect(searchQuery) {
        // Debounce if needed
        songViewModel.setSearchQuery(searchQuery)
    }

    // Handle back press if SearchBar's results overlay is active
    BackHandler(enabled = searchBarIsActive) {
        searchBarIsActive = false
        keyboardController?.hide()
    }
    // Handle back press to collapse the expanded SearchBar itself
    BackHandler(enabled = searchBarExpanded && !searchBarIsActive) {
        searchBarExpanded = false
        searchQuery = "" // Optionally clear query when collapsing
        keyboardController?.hide()
    }

    ModalNavigationDrawer(
        drawerContent = {
            MyDrawerContent(
                onSettingsClick = {
                    navController.navigate(Paths.SettingsPath.route)
                }
            )
        },
        drawerState = drawerState
    ) {
        Scaffold(
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
                            val newSongId = songViewModel.addNewSongAndGetId()
                            navController.navigate(
                                route = Paths.EditSongPath.createRoute(songId = newSongId.toString())
                            )
                        }
                    },
                    onMenuClick = {
                        showOptionsMenu = !showOptionsMenu
                    },
                    showOptionsMenu = showOptionsMenu,
                    onMenuToggle = { showOptionsMenu = !showOptionsMenu }
                )
            },
            floatingActionButton = {
                HomeSortFAB(
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
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
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
                    MainTabs.MY_SONGS -> {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    enableEditing = !enableEditing
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (enableEditing) {
                                        Color.Green
                                    } else {
                                        Color.Red
                                    }
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Song"
                                )
                            }
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(songs.value) { song ->
                                SongItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    songTitle = song.title,
                                    songArtist = song.artist,
                                    onSongClick = {
                                        if (!enableEditing) {
                                            navController.navigate(
                                                Paths.SongPath.createRoute(
                                                    song.id.toString()
                                                )
                                            )
                                        } else {
                                            navController.navigate(
                                                Paths.EditSongPath.createRoute(
                                                    songId = song.id.toString()
                                                )
                                            )
                                        }
                                    },
                                    onDeleteClick = {
                                        scope.launch {
                                            songViewModel.deleteSong(song)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    MainTabs.REMOTE_SONGS -> {
                        Text("Remote Songs: TODO")
                        LaunchedEffect(Unit) {
                            songViewModel.fetchPosts()
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        val remoteSongs = songViewModel.remoteSongs.collectAsState()
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(remoteSongs.value) { song ->
                                RemoteSongItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    song = song.toSongUi(),
                                    onSongClick = {
                                        navController.navigate(Paths.PostPath.createRoute(song.id))
                                    },
                                    onSongSave = {
                                        scope.launch {
                                            songViewModel.saveSongToDatabase(song)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

