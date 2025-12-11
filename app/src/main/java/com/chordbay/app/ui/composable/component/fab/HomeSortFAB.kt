package com.chordbay.app.ui.composable.component.fab

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.chordbay.app.ui.theme.imagevector.Artist
import com.chordbay.app.ui.theme.imagevector.Music_note
import com.chordbay.app.ui.theme.imagevector.Sort


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeSortFAB(
    modifier: Modifier,
    onFabClick: () -> Unit,
    isFabMenuExpanded: Boolean,
    onMenuToggle: () -> Unit,
    sortBy: SortBy,
    onSortSelected: (SortBy) -> Unit,
) {
    val fabItems = SortBy.entries
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
        fabItems.forEach { item ->
            FloatingActionButtonMenuItem(
                onClick = {
                    onSortSelected(item)
                },
                text = {
                    Text(text = item.displayName, color = if (item == sortBy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                },
                icon = {
                    Icon(
                        imageVector = when (item) {
                            SortBy.SONG_NAME -> Music_note
                            SortBy.ARTIST_NAME -> Artist
                        },
                        contentDescription = item.displayName,
                        modifier = Modifier.size(24.dp),
                        tint = if (item == sortBy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                },
                containerColor = if (item == sortBy) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            )
        }
    }
}
