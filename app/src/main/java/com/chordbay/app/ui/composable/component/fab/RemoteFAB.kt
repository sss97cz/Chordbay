package com.chordbay.app.ui.composable.component.fab

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.model.util.SortBy
import com.chordbay.app.ui.composable.screen.song.ResultMode
import com.chordbay.app.ui.composable.screen.song.SortByArtist
import com.chordbay.app.ui.theme.imagevector.Artist
import com.chordbay.app.ui.theme.imagevector.Music_note
import com.chordbay.app.ui.theme.imagevector.Sort

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RemoteFAB(
    modifier: Modifier,
    isFabMenuExpanded: Boolean,
    onMenuToggle: () -> Unit,
    resultMode: ResultMode,
    songSort: SortBy,
    artistSort: SortByArtist,
    onSongItemSelected: (SortBy) -> Unit,
    onArtistItemSelected: (SortByArtist) -> Unit,
) {
    val songItems = SortBy.entries
    val artistItems = SortByArtist.entries

    FloatingActionButtonMenu(
        modifier = modifier.padding(bottom = 16.dp, end = 12.dp),
        expanded = isFabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = isFabMenuExpanded,
                onCheckedChange = { onMenuToggle() },
                containerColor = ToggleFloatingActionButtonDefaults.containerColor(
                    initialColor = MaterialTheme.colorScheme.secondaryContainer,
                    finalColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    imageVector = if (!isFabMenuExpanded) Sort else Icons.Default.Close,
                    contentDescription = "Sort",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    ) {
//        FloatingActionButtonMenuItem(
//            onClick = onAlphabeticalSortClick,
//            text = { Text( text = "Sort", color = if (alphabeticalSort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) },
//            icon = { Icon(Icons.Default.SortByAlpha, contentDescription = "Sort", tint = if (alphabeticalSort) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) },
//            containerColor = if (alphabeticalSort) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
//        )
        when (resultMode) {
            ResultMode.SONGS -> {
                songItems.forEach { item ->
                    FloatingActionButtonMenuItem(
                        onClick = {
                            onSongItemSelected(item)
                        },
                        text = {
                            Text(
                                text = item.displayName,
                                color = if (item == songSort) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (item) {
                                    SortBy.SONG_NAME -> Music_note
                                    SortBy.ARTIST_NAME -> Artist
                                },
                                contentDescription = item.displayName,
                                modifier = Modifier.size(24.dp),
                                tint = if (item == songSort) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        containerColor = if (item != songSort) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }
            ResultMode.ARTISTS -> {
                artistItems.forEach { item ->
                    FloatingActionButtonMenuItem(
                        onClick = {
                            onArtistItemSelected(item)
                        },
                        text = {
                            Text(
                                text = item.title,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (item) {
                                    SortByArtist.ALPHABETICAL -> Icons.Default.SortByAlpha
                                    SortByArtist.MOST_SONGS -> Music_note
                                },
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (item == artistSort) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        containerColor = if (item == artistSort) MaterialTheme.colorScheme.primaryContainer else
                            MaterialTheme.colorScheme.secondaryContainer,
                    )
                }
            }
        }
    }
}
