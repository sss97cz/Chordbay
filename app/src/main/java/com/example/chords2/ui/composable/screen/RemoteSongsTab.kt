package com.example.chords2.ui.composable.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.ui.composable.component.listitem.ArtistItem
import com.example.chords2.ui.composable.component.listitem.RemoteSongItem
import com.example.chords2.ui.composable.component.searchbar.HomeSearchbar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.RemoteSongsViewModel
import com.example.chords2.ui.viewmodel.SongViewModel
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import com.example.chords2.data.helper.pluralText
import com.example.chords2.data.model.Song
import com.example.chords2.data.remote.model.ArtistDto
import com.example.chords2.ui.theme.imagevector.Artist


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemoteSongsTab(
    remoteSongsViewModel: RemoteSongsViewModel,
    songsViewModel: SongViewModel,
    navController: NavController
) {
    val query by remoteSongsViewModel.query.collectAsState()
    val field by remoteSongsViewModel.field.collectAsState()
    val sort by remoteSongsViewModel.sort.collectAsState()
    val artists by remoteSongsViewModel.artists.collectAsState()
    val songs by remoteSongsViewModel.songs.collectAsState()
    val loading by remoteSongsViewModel.loading.collectAsState()
    val error by remoteSongsViewModel.error.collectAsState()

    var isMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val searchOption = remoteSongsViewModel.searchOption.collectAsState()
    val searchbarText by remember {
        derivedStateOf {
            when (searchOption.value) {
                ResultMode.ARTISTS -> "Search artists"
                ResultMode.SONGS -> "Search songs"
            }
        }
    }

    val listPadding = PaddingValues(bottom = 32.dp, top = 4.dp, start = 4.dp, end = 4.dp)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Avoid spamming refresh on rotate when query is blank
    LaunchedEffect(query) {
        if (query.isBlank() && artists.isEmpty()) {
            remoteSongsViewModel.refreshArtists()
        }
    }
    Column(Modifier.fillMaxSize()) {
        if (!isLandscape) {
            // Portrait header
            PortraitSearchBarHeader(
                remoteSongsViewModel = remoteSongsViewModel,
                query = query,
                field = field,
                sort = sort,
                searchOption = searchOption,
                searchbarText = searchbarText,
                isMenuExpanded = isMenuExpanded,
                onMenuExpandedChange = { isMenuExpanded = !isMenuExpanded },
            )
        } else {
            // Landscape header: two columns – search on the left, filters on the right
            LandscapeSearchBarHeader(
                remoteSongsViewModel = remoteSongsViewModel,
                query = query,
                field = field,
                sort = sort,
                searchOption = searchOption,
                searchbarText = searchbarText,
                isMenuExpanded = isMenuExpanded,
                onMenuExpandedChange = { isMenuExpanded = !isMenuExpanded },
            )
        }
        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        ResultHeader(
            mode = searchOption.value,
            count = if (searchOption.value == ResultMode.ARTISTS) artists.size else songs.size,
            query = query.ifBlank { null },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
        HorizontalDivider()
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
        Box(Modifier.weight(1f)) {
            if (isLandscape) {
                GridResultList(
                    query = query,
                    searchOption = searchOption,
                    artists = artists,
                    songs = songs,
                    listPadding = listPadding,
                    navController = navController,
                    remoteSongsViewModel = remoteSongsViewModel
                )
            } else {
                NormalResultList(
                    query = query,
                    searchOption = searchOption,
                    artists = artists,
                    songs = songs,
                    listPadding = listPadding,
                    navController = navController,
                    remoteSongsViewModel = remoteSongsViewModel
                )
            }
        }
    }
}


@Composable
fun PortraitSearchBarHeader(
    remoteSongsViewModel: RemoteSongsViewModel,
    query: String,
    field: FilterField,
    sort: SortBy,
    searchOption: State<ResultMode>,
    searchbarText: String,
    isMenuExpanded: Boolean,
    onMenuExpandedChange: () -> Unit,

    ) {
    // Portrait header
    HomeSearchbar(
        modifier = Modifier.fillMaxWidth(),
        placeholder = searchbarText,
        searchBarExpanded = true,
        searchQuery = query,
        onQueryChange = remoteSongsViewModel::onQueryChanged,
        onSearch = { remoteSongsViewModel.search() },
        onSearchClick = remoteSongsViewModel::search,
        onClearClick = {
            remoteSongsViewModel.onQueryChanged("")
            remoteSongsViewModel.refreshArtists()
        },
    )
    val isSongsSelected = searchOption.value == ResultMode.SONGS
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        ) {
            Text(
                text = "Filter by:",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = field == FilterField.TITLE,
                    onClick = { remoteSongsViewModel.onFieldChanged(FilterField.TITLE) },
                    label = { Text("Title") }
                )
                FilterChip(
                    selected = field == FilterField.ARTIST,
                    onClick = { remoteSongsViewModel.onFieldChanged(FilterField.ARTIST) },
                    label = { Text("Artist") }
                )
                FilterChip(
                    selected = field == FilterField.BOTH,
                    onClick = { remoteSongsViewModel.onFieldChanged(FilterField.BOTH) },
                    label = { Text("Both") }
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            SingleChoiceSegmentedButtonRow {
                ResultMode.entries.forEachIndexed { index, result ->
                    SegmentedButton(
                        modifier = Modifier
                            .size(65.dp, 36.dp)
                            .align(Alignment.Top),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ResultMode.entries.size,
                        ),
                        colors = SegmentedButtonDefaults.colors().copy(
                            inactiveBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            activeBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        onClick = {
                            remoteSongsViewModel.onSearchOptionChange(result)
                        },
                        selected = searchOption.value == result,
                        label = {
                            when (result) {
                                ResultMode.ARTISTS -> Icon(
                                    Artist,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                ResultMode.SONGS -> Icon(
                                    Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }
            AssistChip(
                onClick = {
                    remoteSongsViewModel.onSortChanged(
                        if (sort == SortBy.SONG_NAME) SortBy.ARTIST_NAME else SortBy.SONG_NAME
                    )
                },
                label = {
                    Text(
                        text = if (sort == SortBy.SONG_NAME) "Sort: Title" else "Sort: Artist",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                modifier = Modifier.height(32.dp)
            )
        }
    }

    HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
}

@Composable
fun LandscapeSearchBarHeader(
    remoteSongsViewModel: RemoteSongsViewModel,
    query: String,
    field: FilterField,
    sort: SortBy,
    searchOption: State<ResultMode>,
    searchbarText: String,
    isMenuExpanded: Boolean,
    onMenuExpandedChange: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        HomeSearchbar(
            modifier = Modifier
                .weight(1f),
            placeholder = searchbarText,
            searchBarExpanded = true,
            searchQuery = query,
            onQueryChange = remoteSongsViewModel::onQueryChanged,
            onSearch = { remoteSongsViewModel.search() },
            onSearchClick = remoteSongsViewModel::search,
            onClearClick = {
                remoteSongsViewModel.onQueryChanged("")
                remoteSongsViewModel.refreshArtists()
            },
            trailingContent = {
                Box() {
                    IconButton(
                        onClick = {
                            onMenuExpandedChange()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                        )
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = {
                        }
                    ) {
                        for (option in ResultMode.entries) {
                            val selected = option == searchOption.value
                            val backgroundColor =
                                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else Color.Transparent
                            val contentColor =
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant

                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        backgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                text = {
                                    when (option) {
                                        ResultMode.SONGS -> {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.MusicNote,
                                                    contentDescription = null,
                                                    tint = contentColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = "Search Songs",
                                                    color = contentColor
                                                )
                                            }
                                        }

                                        ResultMode.ARTISTS -> {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Artist,
                                                    contentDescription = null,
                                                    tint = contentColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = "Search Artists",
                                                    color = contentColor
                                                )
                                            }
                                        }
                                    }
                                },
                                onClick = {
                                    remoteSongsViewModel.onSearchOptionChange(option)
                                    onMenuExpandedChange()
                                }
                            )
                        }
                    }
                }
            }
        )
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 4.dp),
        ) {
            Text(
                text = "Filter by",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = field == FilterField.TITLE,
                    onClick = { remoteSongsViewModel.onFieldChanged(FilterField.TITLE) },
                    label = { Text("Title") }
                )
                FilterChip(
                    selected = field == FilterField.ARTIST,
                    onClick = { remoteSongsViewModel.onFieldChanged(FilterField.ARTIST) },
                    label = { Text("Artist") }
                )
                FilterChip(
                    selected = field == FilterField.BOTH,
                    onClick = { remoteSongsViewModel.onFieldChanged(FilterField.BOTH) },
                    label = { Text("Both") }
                )
                AssistChip(
                    onClick = {
                        remoteSongsViewModel.onSortChanged(
                            if (sort == SortBy.SONG_NAME) SortBy.ARTIST_NAME else SortBy.SONG_NAME
                        )
                    },
                    label = { Text(if (sort == SortBy.SONG_NAME) "Sort: Title" else "Sort: Artist") }
                )
            }
        }
    }
}

@Composable
fun GridResultList(
    query: String,
    searchOption: State<ResultMode>,
    artists: List<ArtistDto>,
    songs: List<Song>,
    listPadding: PaddingValues,
    navController: NavController,
    remoteSongsViewModel: RemoteSongsViewModel,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = listPadding
    ) {
        if (searchOption.value == ResultMode.ARTISTS) {
            if (artists.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    NothingFoundPrompt(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        title = "No artists found",
                        message = "Try a different query"
                    )
                }
            }
            items(artists) { artist ->
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
        } else {
            if (query.isBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        SongsQueryPrompt(modifier = Modifier.fillMaxSize())
                    }
                }
            } else {
                if (songs.isEmpty()){
                    item(span = { GridItemSpan(2) }) {
                        NothingFoundPrompt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            title = "No songs found",
                            message = "Try a different query"
                        )
                    }
                }
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
                        onLongClick = { },
                        onDownloadClick = {
                            remoteSongsViewModel.saveSong(song)
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun NormalResultList(
    query: String,
    searchOption: State<ResultMode>,
    artists: List<ArtistDto>,
    songs: List<Song>,
    listPadding: PaddingValues,
    navController: NavController,
    remoteSongsViewModel: RemoteSongsViewModel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = listPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (searchOption.value == ResultMode.ARTISTS) {
            if (artists.isEmpty()) {
                item {
                    NothingFoundPrompt(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        title = "No artists found",
                        message = "Try a different query"
                    )
                }
            }
            items(artists) { artist ->
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
        } else {
            if (query.isBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        SongsQueryPrompt(modifier = Modifier.fillMaxSize())
                    }
                }
            } else {
                if (songs.isEmpty()){
                    item {
                        NothingFoundPrompt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            title = "No songs found",
                            message = "Try a different query"
                        )
                    }
                }
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
                        onLongClick = { },
                        onDownloadClick = {
                            remoteSongsViewModel.saveSong(song)
                        },
                    )
                }
            }
        }
    }
}

enum class ResultMode { SONGS, ARTISTS }

@Composable
fun ResultHeader(mode: ResultMode, count: Int, query: String?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Crossfade(targetState = mode) { m ->
            val icon = if (m == ResultMode.ARTISTS) Artist else Icons.Default.MusicNote
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(Modifier.width(12.dp))

        Column() {
            Text(
                text = if (mode == ResultMode.ARTISTS) "Artists" else "Songs",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$count ${pluralText("result", count)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SongsQueryPrompt(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .wrapContentSize()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Type something into searchbar to see individual songs",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Try a title or artist name",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun NothingFoundPrompt(
    modifier: Modifier = Modifier,
    title: String = "No results found",
    message: String = "Try a different query"
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .wrapContentSize()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(56.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

data class FilterField(val value: String) {
    companion object {
        val TITLE = FilterField("title")
        val ARTIST = FilterField("artist")
        val BOTH = FilterField("both")
    }
}