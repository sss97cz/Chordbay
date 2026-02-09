package com.chordbay.app.ui.composable.screen.song

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chordbay.app.data.helper.pluralText
import com.chordbay.app.data.model.Song
import com.chordbay.app.data.model.util.SortBy
import com.chordbay.app.data.remote.model.ArtistDto
import com.chordbay.app.ui.composable.component.listitem.ArtistItem
import com.chordbay.app.ui.composable.component.listitem.RemoteSongItem
import com.chordbay.app.ui.composable.component.searchbar.HomeSearchbar
import com.chordbay.app.ui.composable.navigation.Paths
import com.chordbay.app.ui.theme.imagevector.Artist
import com.chordbay.app.ui.viewmodel.RemoteSongsViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemoteSongsTab(
    remoteSongsViewModel: RemoteSongsViewModel,
    navController: NavController
) {
    val query by remoteSongsViewModel.query.collectAsState()
    val field by remoteSongsViewModel.field.collectAsState()
    val sort by remoteSongsViewModel.sortSongs.collectAsState()
    val artists by remoteSongsViewModel.artists.collectAsState()
    val songs by remoteSongsViewModel.songs.collectAsState()
    val loading by remoteSongsViewModel.loading.collectAsState()
    val error by remoteSongsViewModel.error.collectAsState()
    val artistFirstLetters by remoteSongsViewModel.artistFirstLetters.collectAsState()

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

    LaunchedEffect(query) {
        if (query.isBlank() && artists.isEmpty()) {
            remoteSongsViewModel.refreshArtists()
        }
    }
    Column(Modifier.fillMaxSize()) {
        if (!isLandscape) {
            PortraitSearchBarHeader(
                remoteSongsViewModel = remoteSongsViewModel,
                query = query,
                field = field,
                sort = sort,
                searchOption = searchOption,
                searchbarText = searchbarText,
                isMenuExpanded = isMenuExpanded,
                onMenuExpandedChange = { isMenuExpanded = !isMenuExpanded },
                artistFirstLetters = artistFirstLetters
            )
        } else {
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
        var isRefreshing by remember { mutableStateOf(false) }
        var lastRefreshTime by remember { mutableLongStateOf(0L) }

        PullToRefreshBox(
            modifier = Modifier.weight(1f),
            isRefreshing = isRefreshing,
            onRefresh = {
                val now = System.currentTimeMillis()
                if (now - lastRefreshTime < 300L) return@PullToRefreshBox
                lastRefreshTime = now
                isRefreshing = true
                when (remoteSongsViewModel.searchOption.value) {
                    ResultMode.ARTISTS -> {
                        remoteSongsViewModel.refreshArtists()
                    }

                    ResultMode.SONGS -> {
                        val q = remoteSongsViewModel.query.value
                        if (q.isBlank()) {
                            if (remoteSongsViewModel.showMostViewed.value) {
                                remoteSongsViewModel.onShowMostViewedClick()
                                remoteSongsViewModel.onShowMostViewedClick()
                            }
                        } else {
                            remoteSongsViewModel.search()
                        }
                    }
                }
                isRefreshing = false
            }
        ) {
            Box() {
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
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PortraitSearchBarHeader(
    remoteSongsViewModel: RemoteSongsViewModel,
    query: String,
    field: FilterField,
    sort: SortBy,
    searchOption: State<ResultMode>,
    searchbarText: String,
    artistFirstLetters: List<Char>,
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
        trailingContent = {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(end = 0.dp).offset(x = 2.dp),
            ) {
                ResultMode.entries.forEachIndexed { index, result ->
                    SegmentedButton(
                        modifier = Modifier
                            .size(60.dp, 52.dp)
                            .align(Alignment.Top),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = ResultMode.entries.size,
                            baseShape = if (index == 1) {
                                MaterialTheme.shapes.extraLargeIncreased
                            } else {
                                MaterialTheme.shapes.large
                            }
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
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxHeight()
                                )

                                ResultMode.SONGS -> Icon(
                                    Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxHeight()

                                )
                            }
                        }
                    )
                }
            }
        }
    )
    val isSongsSelected = searchOption.value == ResultMode.SONGS
    val selectedLetter =
        remoteSongsViewModel.artistFirstLetterFilterChipSelected.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .height(80.dp)
            .padding(horizontal = 8.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
  //      horizontalArrangement = Arrangement.SpaceBetween
    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxHeight()
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
//                    shape = MaterialTheme.shapes.medium
//                )
//                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
//        ) {
//            Text(
//                text = "Filter options:",
//                style = MaterialTheme.typography.labelMedium.copy(
//                    fontWeight = FontWeight.SemiBold,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                ),
//                modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
//            )
//
            ChipRow(
                modifier = Modifier.weight(1f),
                onFilterSongClick = { remoteSongsViewModel.onFieldChanged(it) },
                onFilterArtistClick = {
                    remoteSongsViewModel.onArtistFirstLetterFilterChange(
                        it
                    )
                },
                field = field,
                isSongsSelected = isSongsSelected,
                selectedLetter = selectedLetter,
                artistFirstLetters = artistFirstLetters
            )
//        }
        val sortByArtist = remoteSongsViewModel.sortArtists.collectAsState()
//        Column(
//            modifier = Modifier.fillMaxHeight(),
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.End
//        ) {
//            SingleChoiceSegmentedButtonRow {
//                ResultMode.entries.forEachIndexed { index, result ->
//                    SegmentedButton(
//                        modifier = Modifier
//                            .size(75.dp, 36.dp)
//                            .align(Alignment.Top),
//                        shape = SegmentedButtonDefaults.itemShape(
//                            index = index,
//                            count = ResultMode.entries.size,
//                        ),
//                        colors = SegmentedButtonDefaults.colors().copy(
//                            inactiveBorderColor = MaterialTheme.colorScheme.surfaceVariant,
//                            activeBorderColor = MaterialTheme.colorScheme.surfaceVariant,
//                        ),
//                        onClick = {
//                            remoteSongsViewModel.onSearchOptionChange(result)
//                        },
//                        selected = searchOption.value == result,
//                        label = {
//                            when (result) {
//                                ResultMode.ARTISTS -> Icon(
//                                    Artist,
//                                    contentDescription = null,
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                                    modifier = Modifier.fillMaxHeight()
//                                )
//
//                                ResultMode.SONGS -> Icon(
//                                    Icons.Default.MusicNote,
//                                    contentDescription = null,
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                                    modifier = Modifier.fillMaxHeight()
//
//                                )
//                            }
//                        }
//                    )
//                }
//            }
        if (isSongsSelected) {
            Row(
//                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                    val showMostViewed =
                        remoteSongsViewModel.showMostViewed.collectAsState()
                    FilterChip(
                        selected = showMostViewed.value,
                        onClick = {
                            remoteSongsViewModel.onShowMostViewedClick()
                        },
                        label = {
                            Text(
                                text = "Popular",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier
                            .height(32.dp)
                            .widthIn(min = 72.dp)
                    )
                }
//                AssistChip(
//                    onClick = {
//                        remoteSongsViewModel.onSortChanged(searchOption.value)
//                    },
//                    label = {
//                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
//                            Text(
//                                text = if (searchOption.value == ResultMode.SONGS) {
//                                    when (sort) {
//                                        SortBy.SONG_NAME -> "Sort: Title"
//                                        SortBy.ARTIST_NAME -> "Sort: Artist"
//                                    }
//                                } else {
//                                    when (sortByArtist.value) {
//                                        SortByArtist.ALPHABETICAL -> "Sort: A-Z"
//                                        SortByArtist.MOST_SONGS -> "Sort: Songs"
//                                    }
//                                },
//                                style = MaterialTheme.typography.labelSmall,
//                                textAlign = TextAlign.Center,
////                            modifier = Modifier.padding(horizontal = 4.dp)
//                            )
//                        }
//                    },
//                    modifier = Modifier
//                        .height(32.dp)
//                        .width(95.dp)
//                )
            }
        }
//    }
    HorizontalDivider(Modifier.padding(top = 2.dp))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
            .fillMaxWidth().padding(end = 4.dp, start = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        HomeSearchbar(
            modifier = Modifier
                .weight(3f),
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
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.padding(end = 0.dp).offset(x = 2.dp),
                ) {
                    ResultMode.entries.forEachIndexed { index, result ->
                        SegmentedButton(
                            modifier = Modifier
                                .size(60.dp, 52.dp)
                                .align(Alignment.Top),
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ResultMode.entries.size,
                                baseShape = if (index == 1) {
                                    MaterialTheme.shapes.extraLargeIncreased
                                } else {
                                    MaterialTheme.shapes.large
                                }
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.fillMaxHeight()
                                    )

                                    ResultMode.SONGS -> Icon(
                                        Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.fillMaxHeight()

                                    )
                                }
                            }
                        )
                    }
                }
            }
        )
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .wrapContentWidth()
//                .padding(horizontal = 4.dp),
//        ) {
//            Text(
//                text = "Filter options:",
//                style = MaterialTheme.typography.bodySmall,
//                modifier = Modifier.padding(top = 8.dp),
//            )
            FlowRow(
                modifier = Modifier
                    .weight(2f).padding(start = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChipRow(
                    onFilterSongClick = { remoteSongsViewModel.onFieldChanged(it) },
                    onFilterArtistClick = {
                        remoteSongsViewModel.onArtistFirstLetterFilterChange(
                            it
                        )
                    },
                    field = field,
                    isSongsSelected = searchOption.value == ResultMode.SONGS,
                    selectedLetter = remoteSongsViewModel.artistFirstLetterFilterChipSelected.collectAsState(),
                    artistFirstLetters = remoteSongsViewModel.artistFirstLetters.collectAsState().value
                )
                val showMostViewed = remoteSongsViewModel.showMostViewed.collectAsState()
                val sortByArtist = remoteSongsViewModel.sortArtists.collectAsState()
                if (searchOption.value == ResultMode.SONGS) {
                    Spacer(Modifier.weight(1f))
                    FilterChip(
                        selected = showMostViewed.value,
                        onClick = {
                            remoteSongsViewModel.onShowMostViewedClick()
                        },
                        label = {
                            Text(
                                text = "Popular",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.widthIn(min = 72.dp).padding(end = 4.dp),
                        colors = FilterChipDefaults.filterChipColors().copy(

                        )
                    )
                }
//                AssistChip(
//                    onClick = {
//                        remoteSongsViewModel.onSortChanged(searchOption.value)
//                    },
//                    label = {
//                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
//                            Text(
//                                text = if (searchOption.value == ResultMode.SONGS) {
//                                    when (sort) {
//                                        SortBy.SONG_NAME -> "Sort: Title"
//                                        SortBy.ARTIST_NAME -> "Sort: Artist"
//                                    }
//                                } else {
//                                    when (sortByArtist.value) {
//                                        SortByArtist.ALPHABETICAL -> "Sort: A-Z"
//                                        SortByArtist.MOST_SONGS -> "Sort: Songs"
//                                    }
//                                },
//                                textAlign = TextAlign.Center,
//                                style = MaterialTheme.typography.labelSmallEmphasized,
//                            )
//                        }
//                    },
//                    modifier = Modifier.width(95.dp)
//                )
            }
        }
//    }
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
    val mostViewed = remoteSongsViewModel.showMostViewed.collectAsState()
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
                item(
                    span = { GridItemSpan(2) }) {
                    NothingFoundPrompt(
                        modifier = Modifier.fillMaxWidth(),
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
            if (songs.isEmpty()) {
                if (query.isNotBlank()) {
                    item(span = { GridItemSpan(2) }) {
                        NothingFoundPrompt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            title = "No songs found",
                            message = "Try a different query"
                        )
                    }
                } else {
                    item(span = { GridItemSpan(2) }) {
                        SongsQueryPrompt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )
                    }
                }
            }

        }
        items(songs) { song ->
            RemoteSongItem(
                songTitle = song.title,
                songArtist = song.artist,
                isSynced = song.markSynced,
                onSongClick = {
                    remoteSongsViewModel.clearSaveSuccess()
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
                        remoteSongsViewModel.clearSaveSuccess()
                        navController.navigate(
                            Paths.ArtistSongsPath.createRoute(artist.name)
                        )
                    }
                )
            }
        } else {
            if (songs.isEmpty()) {
                if (query.isBlank()) {
                    item {
                        SongsQueryPrompt(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )
                    }
                } else {
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
            }
            items(songs) { song ->
                RemoteSongItem(
                    songTitle = song.title,
                    songArtist = song.artist,
                    isSynced = song.markSynced,
                    onSongClick = {
                        remoteSongsViewModel.clearSaveSuccess()
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

@Composable
fun ResultHeader(
    mode: ResultMode,
    count: Int,
    query: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Crossfade(targetState = mode) { m ->
            val icon = if (m == ResultMode.ARTISTS) Artist else Icons.Default.MusicNote
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
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
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Try a title or artist name",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
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
        contentAlignment = Alignment.Center,
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
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ChipRow(
    modifier: Modifier = Modifier,
    onFilterSongClick: (FilterField) -> Unit,
    onFilterArtistClick: (Char?) -> Unit,
    field: FilterField,
    isSongsSelected: Boolean,
    selectedLetter: State<Char?>,
    artistFirstLetters: List<Char>,
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.widthIn(min = 200.dp, max = 350.dp),
        //contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        if (isSongsSelected) {
            for (selected in FilterField.entries) {
                item {
                    FilterChip(
                        selected = field == selected,
                        onClick = { onFilterSongClick(selected) },
                        label = { Text(selected.title) }
                    )
                }
            }
        } else {
            items(artistFirstLetters) {
                FilterChip(
                    selected = selectedLetter.value == it,
                    onClick = {
                        Log.d(
                            "RemoteSongsTab",
                            "Artist first letter filter chip clicked: $it"
                        )
                        onFilterArtistClick(
                            if (selectedLetter.value == it) null else it
                        )
                    },
                    label = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(20.dp)
                        ){
                            Text(
                                text = it.toString(),
                            )
                        }
                    }
                )
            }
        }
    }
}

enum class FilterField(val title: String) { TITLE("Title"), ARTIST("Artist"), BOTH("Both") }
enum class ResultMode(val displayName: String) { SONGS("Search Songs"), ARTISTS("Search Artists") }

enum class SortByArtist(val title: String) {
    ALPHABETICAL("Name"), MOST_SONGS("Most Songs")
}
