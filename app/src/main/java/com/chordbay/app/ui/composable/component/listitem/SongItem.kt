package com.chordbay.app.ui.composable.component.listitem

import android.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SongItem(
    modifier: Modifier = Modifier,
    songTitle: String,
    songArtist: String,
    trailingContent: @Composable (() -> Unit)? = null,
    onSongClick: () -> Unit,
    onLongClick: () -> Unit,
    isSelected: Boolean = false,
    isDragging: Boolean = false
) {
    val colors = CardDefaults.cardColors(
        containerColor = if (isSelected) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            if (isDragging) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        },
        contentColor = if (isSelected) {
            MaterialTheme.colorScheme.onTertiaryContainer
        } else {
            if (isDragging) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        }
    )

    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                },
                shape = MaterialTheme.shapes.medium
            ),
        colors = colors,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
                .combinedClickable(
                    onClick = onSongClick,
                    onLongClick = onLongClick
                )
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .heightIn(min = 60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = songTitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = songArtist,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (trailingContent != null) {
                    Spacer(Modifier.width(8.dp))
                    trailingContent()
                }
            }
        }
    }
}
