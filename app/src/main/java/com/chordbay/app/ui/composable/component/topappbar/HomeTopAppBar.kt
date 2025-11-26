package com.chordbay.app.ui.composable.component.topappbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.chordbay.app.data.model.util.MainTabs
import com.chordbay.app.ui.theme.imagevector.Playlist_add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    searchBarExpanded: Boolean,
    selectedTab: MainTabs,
    onNavigationIconClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit,
    onMenuClick: () -> Unit,
    showOptionsMenu: Boolean,
    onMenuToggle: () -> Unit,
    onPlaylistClick: () -> Unit,
    onSyncClick: () -> Unit,
    onImportTxtClick: () -> Unit
) {
    MyTopAppBar(
        title = when (selectedTab){
            MainTabs.MY_SONGS -> if (searchBarExpanded) "Search Songs" else MainTabs.MY_SONGS.title
            MainTabs.REMOTE_SONGS -> MainTabs.REMOTE_SONGS.title
        },
        navigationIcon = Icons.Filled.Menu,
        onNavigationIconClick = onNavigationIconClick,
        actions = {
            if (selectedTab == MainTabs.MY_SONGS) {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Filled.Search, contentDescription = "Search Songs")
                }
            }
            IconButton(onClick = onAddClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add Song")
            }
            Box {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }
                DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = onMenuToggle
                ) {
                    DropdownMenuItem(
                        text = { Text("New Playlist") },
                        onClick = onPlaylistClick,
                        leadingIcon = { Icon(Playlist_add, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Sync") },
                        onClick = onSyncClick,
                        leadingIcon = { Icon(Icons.Filled.Sync, contentDescription = null) }
                    )
                    if (selectedTab == MainTabs.MY_SONGS) {
                        DropdownMenuItem(
                            text = { Text("Import TXT") },
                            onClick = onImportTxtClick,
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Input, contentDescription = null) }
                        )
                    }
//                    HorizontalDivider()
//                    DropdownMenuItem(
//                        text = { Text("Help") },
//                        onClick = { /* Handle about */ },
//                        leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) }
//                    )
                }
            }
        },
    )
}