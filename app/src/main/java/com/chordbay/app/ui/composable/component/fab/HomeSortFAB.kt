package com.chordbay.app.ui.composable.component.fab

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.model.util.SortBy
import com.chordbay.app.ui.theme.imagevector.Artist
import com.chordbay.app.ui.theme.imagevector.Music_note
import com.chordbay.app.ui.theme.imagevector.Sort


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
//    FloatingActionButton(
//        modifier = modifier.padding(bottom = 32.dp, end = 16.dp),
//        shape = CircleShape,
//        onClick = onFabClick,
//        containerColor = MaterialTheme.colorScheme.secondaryContainer,
//        contentColor = MaterialTheme.colorScheme.secondary
//    ) {
//        Icon(
//            imageVector = Sort,
//            contentDescription = "Sort",
//            modifier = Modifier.size(24.dp),
//            tint = MaterialTheme.colorScheme.onSurface
//        )
//    }
//    DropdownMenu(
//        modifier = Modifier.width(180.dp),
//        offset = DpOffset(x = (-8).dp, y = (-8).dp),
//        expanded = isFabMenuExpanded,
//        onDismissRequest = onMenuToggle,
//    ) {
//        Text(
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .padding(vertical = 8.dp),
//            text = "Sort by:",
//            style = MaterialTheme.typography.titleMedium
//        )
//        HorizontalDivider()
//        for (option in SortBy.entries) {
//            val isSelected = option == sortBy
//            DropdownMenuItem(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(
//                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
//                        else Color.Transparent,
//                        shape = RoundedCornerShape(8.dp)
//                    ),
//                text = {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.fillMaxWidth(),
//                    ) {
//                        // Replace with your own icons for each sort option
//                        Icon(
//                            imageVector = when (option) {
//                                SortBy.SONG_NAME -> Music_note
//                                SortBy.ARTIST_NAME -> Artist
//                            },
//                            contentDescription = option.displayName,
//                            modifier = Modifier.size(24.dp),
//                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
//                        )
//                        Spacer(Modifier.width(12.dp))
//                        Text(
//                            text = option.displayName,
//                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//                },
//                onClick = {
//                    onSortSelected(option)
//                }
//            )
//        }
//    }
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
