package com.chordbay.app.ui.composable.component.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchQuery(
    modifier: Modifier = Modifier,
    query: String,
    onClearClick: () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = if (query.isNotBlank()) "Search query: $query" else "Empty search" }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search icon",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = query.ifBlank { "Search" },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (query.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (query.isNotBlank()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchQueryPreview() {
    SearchQuery(
        modifier = Modifier,
        query = "Hello World ",
        onClearClick = {},
        onClick = {}
    )
}
