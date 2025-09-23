package com.example.chords2.ui.composable.topappbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.chords2.ui.composable.navigation.Paths
import kotlinx.coroutines.launch

@Composable
fun HomeTopAppBar(
    title: String,
    onNavigationIconClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit,
    onMenuClick: () -> Unit,
    showOptionsMenu: Boolean,
    onMenuToggle: () -> Unit,
) {
    MyTopAppBar(
        title = title,
        navigationIcon = Icons.Filled.Menu,
        onNavigationIconClick = onNavigationIconClick,
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Filled.Search, contentDescription = "Search Songs")
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
                        text = { Text("Settings") },
                        onClick = { /* Handle settings */ },
                        leadingIcon = { Icon(Icons.Filled.Refresh, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("About") },
                        onClick = { /* Handle about */ },
                        leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) }
                    )
                    Divider()
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
}