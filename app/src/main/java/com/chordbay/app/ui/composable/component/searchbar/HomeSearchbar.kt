package com.chordbay.app.ui.composable.component.searchbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchbar(
    modifier: Modifier = Modifier,
    placeholder: String = "Search songs or artists...",
    searchBarExpanded: Boolean,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSearchClick: () -> Unit,
    onClearClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = searchBarExpanded,
        enter = fadeIn(animationSpec = tween(50)) + expandVertically(animationSpec = tween(100)),
        exit = fadeOut(animationSpec = tween(50)) + shrinkVertically(animationSpec = tween(100))
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(bottom = 4.dp)
        ) {
            val colors1 = SearchBarDefaults.colors()
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = onQueryChange,
                        onSearch = { onSearch(it) },
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text(placeholder) },
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
                            } else {
                                if (trailingContent != null) {
                                    trailingContent()
                                }
                            }
                        },
                        colors = colors1.inputFieldColors,
                    )
                },
                expanded = false,
                onExpandedChange = {},
                modifier = modifier.fillMaxWidth(),
                shape = SearchBarDefaults.inputFieldShape,
                colors = colors1,
                tonalElevation = SearchBarDefaults.TonalElevation,
                shadowElevation = SearchBarDefaults.ShadowElevation,
                windowInsets = WindowInsets(top = 0),
                content = { },
            )
        }
    }
}
@Composable
@Preview(showBackground = true)
fun HomeSearchbarPreview() {
    HomeSearchbar(
        searchBarExpanded = true,
        searchQuery = "Hello World",
        onQueryChange = {},
        onSearch = {},
        onSearchClick = {},
        onClearClick = {},
    )
}
