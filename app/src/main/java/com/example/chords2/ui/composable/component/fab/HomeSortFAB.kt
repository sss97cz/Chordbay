package com.example.chords2.ui.composable.component.fab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.ui.theme.imagevector.Sort

@Composable
fun HomeSortFAB(
    onFabClick: () -> Unit,
    isFabMenuExpanded: Boolean,
    onMenuToggle: () -> Unit,
    sortBy: SortBy,
    onSortSelected: (SortBy) -> Unit,
) {
    Box {
        FloatingActionButton(
            shape = CircleShape,
            onClick = onFabClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                imageVector = Sort,
                contentDescription = "Small floating action button.",
                modifier = Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            modifier = Modifier
                .width(200.dp),
            expanded = isFabMenuExpanded,
            onDismissRequest = onMenuToggle,
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 4.dp),
                text = "Sort by:"
            )
            HorizontalDivider()
            for (option in SortBy.entries) {
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = if(option == sortBy) "* ${option.displayName}" else option.displayName,
                            textAlign = TextAlign.Center
                        )
                    },
                    onClick = {
                        onSortSelected(option)
//                        onMenuToggle()
                    }
                )
            }
        }
    }
}
