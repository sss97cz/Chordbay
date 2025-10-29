package com.example.chords2.ui.composable.component.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.ui.composable.component.listitem.SongItem
import java.text.Normalizer

private fun initialCharOf(value: String): Char {
    if (value.isBlank()) return '#'
    val first = value.trim().first()
    val normalized = Normalizer
        .normalize(first.toString(), Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .first()
    return if (normalized.isLetter()) normalized.uppercaseChar() else '#'
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlphabeticalSongList(
    songs: List<Song>,
    selectedSongs: List<Song>,
    sortBy: SortBy,
    onSongClick: (Song) -> Unit,
    onSongLongClick: (Song) -> Unit,
    bottomPadding: Dp
) {
    val listState = rememberLazyListState()

    val letterSource: (Song) -> String = when (sortBy) {
        SortBy.ARTIST_NAME -> { s -> s.artist }
        SortBy.SONG_NAME -> { s -> s.title }
    }

    val grouped = remember(songs, sortBy) {
        val map = linkedMapOf<Char, MutableList<Song>>()
        songs.forEach { song ->
            val letter = initialCharOf(letterSource(song))
            map.getOrPut(letter) { mutableListOf() }.add(song)
        }
        map.toList()
            .sortedBy { (k, _) -> if (k == '#') '{' else k }
            .toMap(LinkedHashMap())
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 4.dp, bottom = bottomPadding)
        ) {
            grouped.forEach { (letter, list) ->
                stickyHeader {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = letter.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                items(
                    items = list,
                    key = { song -> (song.localId ?: song.hashCode()) }
                ) { song ->
                    SongItem(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth(),
                        songTitle = song.title,
                        songArtist = song.artist,
                        onSongClick = { onSongClick(song) },
                        onLongClick = { onSongLongClick(song) },
                        isSelected = selectedSongs.contains(song),
                        trailingContent = {
                            if (song.markSynced) {
                                Icon(
                                    modifier = Modifier
                                        .height(20.dp),
                                    imageVector = Icons.Default.CloudDone,
                                    contentDescription = "Synced",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
