package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.ui.composable.component.list.AlphabeticalSongList
import com.example.chords2.ui.composable.component.listitem.ArtistItem
import com.example.chords2.ui.composable.component.listitem.RemoteSongItem
import com.example.chords2.ui.composable.component.listitem.SongItem
import com.example.chords2.ui.composable.component.searchbar.HomeSearchbar
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.viewmodel.RemoteSongsViewModel
import com.example.chords2.ui.viewmodel.SongViewModel


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

    LaunchedEffect(query) {
        if (query.isBlank()) {
            remoteSongsViewModel.refreshArtists()
        }
    }


    Column(Modifier.fillMaxSize()) {
        HomeSearchbar(
            searchBarExpanded = true,
            searchQuery = query,
            onQueryChange = remoteSongsViewModel::onQueryChanged,
            onSearch = { remoteSongsViewModel.search() },
            onSearchClick = remoteSongsViewModel::search,
            onClearClick = {
                remoteSongsViewModel.onQueryChanged("")
                remoteSongsViewModel.refreshArtists()
            }
        )

        // Chips row: Field + Sort
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            Spacer(Modifier.weight(1f))
            AssistChip(
                onClick = {
                    remoteSongsViewModel.onSortChanged(
                        if (sort == SortBy.SONG_NAME) SortBy.ARTIST_NAME else SortBy.SONG_NAME
                    )
                },
                label = { Text(if (sort == SortBy.SONG_NAME) "Sort: Title" else "Sort: Artist") }
            )
        }

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (query.isBlank()) {
            // Artists mode
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
            }
        } else {
            // Global search results mode
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                        onLongClick = {  },
                        onDownloadClick = {
                            remoteSongsViewModel.saveSong(song)
                        },
                    )
                }
            }
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