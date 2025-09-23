package com.example.chords2.ui.composable.component.fab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.ui.theme.imagevector.Artist
import com.example.chords2.ui.theme.imagevector.Music_note
import com.example.chords2.ui.theme.imagevector.Sort


@Composable
fun HomeSortFAB(
    modifier: Modifier,
    onFabClick: () -> Unit,
    isFabMenuExpanded: Boolean,
    onMenuToggle: () -> Unit,
    sortBy: SortBy,
    onSortSelected: (SortBy) -> Unit,
) {
    FloatingActionButton(
        modifier = modifier.padding(bottom = 32.dp, end = 16.dp),
        shape = CircleShape,
        onClick = onFabClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(
            imageVector = Sort,
            contentDescription = "Sort",
            modifier = Modifier.size(24.dp)
        )
    }
    DropdownMenu(
        modifier = Modifier.width(180.dp),
        offset = DpOffset(x = (-8).dp, y = (-8).dp),
        expanded = isFabMenuExpanded,
        onDismissRequest = onMenuToggle,
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            text = "Sort by:",
            style = MaterialTheme.typography.titleMedium
        )
        HorizontalDivider()
        for (option in SortBy.entries) {
            val isSelected = option == sortBy
            DropdownMenuItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    ),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // Replace with your own icons for each sort option
                        Icon(
                            imageVector = when (option) {
                                SortBy.SONG_NAME -> Music_note
                                SortBy.ARTIST_NAME -> Artist
                            },
                            contentDescription = option.displayName,
                            modifier = Modifier.size(24.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = option.displayName,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                onClick = {
                    onSortSelected(option)
                }
            )
        }
    }
}
