package com.example.chords2.ui.composable.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.requestFocus
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.SortBy
import com.example.chords2.ui.composable.component.SongItem
import com.example.chords2.ui.composable.imagevector.Sort
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.composable.topappbar.MyTopAppBar
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

    var selectedTab by remember { mutableIntStateOf(0) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }


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
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Drawer Title",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider()

                    Text(
                        "Section 1",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text("Item 1") },
                        selected = false,
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Item 2") },
                        selected = false,
                        onClick = { /* Handle click */ }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "Section 2",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                        badge = { Text("hahaah") }, // Placeholder
                        onClick = { /* Handle click */ }
                    )
                    NavigationDrawerItem(
                        label = { Text("Help and feedback") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                        onClick = { /* Handle click */ },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                MyTopAppBar(
                    title = "My Songs",
                    // No navigation icon if it's the start destination without a drawer
                    // Or, if you have a drawer:
                    navigationIcon = Icons.Filled.Menu,
                    onNavigationIconClick = {
                        scope.launch {
                            if (drawerState.isClosed) {
                                drawerState.open()
                            } else {
                                drawerState.close()
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            searchBarExpanded = !searchBarExpanded
                        }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search Songs")
                        }
                        IconButton(onClick = {
                            scope.launch {
                                val newSongId = songViewModel.addNewSongAndGetId()
                                navController.navigate(Paths.EditSongPath.createRoute(newSongId.toString()))
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Song")
                        }
                        Box {
                            IconButton(onClick = { showOptionsMenu = !showOptionsMenu }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                expanded = showOptionsMenu,
                                onDismissRequest = { showOptionsMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Option 1") },
                                    onClick = { /* Do something... */ }
                                )
                                DropdownMenuItem(
                                    text = { Text("Option 2") },
                                    onClick = { /* Do something... */ }
                                )
                            }
                        }
                    },
                )
            },
            floatingActionButton = {
                Box {
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = {
                            showFabMenu = !showFabMenu
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = Sort,
                            contentDescription = "Small floating action button.",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(
                        modifier = Modifier
                            .width(200.dp),
                        expanded = showFabMenu,
                        onDismissRequest = { showFabMenu = false },
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 4.dp),
                            text = "Sort by:"
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Artist",
                                    textAlign = TextAlign.Center
                                )
                            },
                            onClick = {
                                songViewModel.setSortOption(SortBy.ARTIST_NAME)
                                showFabMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Song name",
                                    textAlign = TextAlign.Center,
                                )
                            },
                            onClick = {
                                songViewModel.setSortOption(SortBy.SONG_NAME)
                                showFabMenu = false
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                PrimaryTabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTab,
                ) {
                    Tab(
                        onClick = {
                            selectedTab = 0
                        },
                        selected = selectedTab == 0,
                        text = {
                            Text("Tab 1")
                        }
                    )
                    Tab(
                        onClick = {
                            selectedTab = 1
                        },
                        selected = selectedTab == 1,
                        text = {
                            Text("Tab 2")
                        }
                    )
                }
                if (searchBarExpanded) {
                    Box {
                        SearchBar(
                            query = searchQuery,
                            windowInsets = WindowInsets(top = 0),
                            onQueryChange = { searchQuery = it },
                            onSearch = {
                                keyboardController?.hide() // Hide keyboard on search action
                                // No other action needed here as filtering is live
                            },
                            active = false, // Crucial: Keep 'active' state false
                            onActiveChange = {
                                // Do nothing here, or only minimal logic if needed.
                                // We don't want it to change 'active' state to true
                                // which would trigger the overlay.
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search songs or artists...") },
                            leadingIcon = {
                                IconButton(
                                    onClick = {
                                        searchBarExpanded = false
                                    }
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = "Search Songs")
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            },
                        ) {}
                    }
                }
                //
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
                        Image(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Song"
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(songs.value) { songEntity ->
                        SongItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            songTitle = songEntity.title,
                            songArtist = songEntity.artist,
                            onSongClick = {
                                if (!enableEditing) {
                                    navController.navigate(
                                        Paths.SongPath.createRoute(
                                            songEntity.id.toString()
                                        )
                                    )
                                } else {
                                    navController.navigate(
                                        Paths.EditSongPath.createRoute(
                                            songId = songEntity.id.toString()
                                        )
                                    )
                                }
                            },
                            onDeleteClick = {
                                scope.launch {
                                    songViewModel.deleteSong(songEntity)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
