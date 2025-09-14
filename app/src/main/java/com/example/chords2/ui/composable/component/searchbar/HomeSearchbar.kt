package com.example.chords2.ui.composable.component.searchbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchbar(
    searchBarExpanded: Boolean,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Box {
        SearchBar(
            query = searchQuery,
            windowInsets = WindowInsets(top = 0),
            onQueryChange = onQueryChange,
            onSearch = { onSearch(it) },
            active = false, // Crucial: Keep 'active' state false
            onActiveChange = {
                // Do nothing here, or only minimal logic if needed.
                // We don't want it to change 'active' state to true
                // which would trigger the overlay.
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search songs or artists...") },
            leadingIcon = {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = "Search Songs")
                }
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
        ) {}
    }
}