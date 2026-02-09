package com.chordbay.app.ui.composable.component.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.model.Song
import com.chordbay.app.ui.composable.component.listitem.SongItem
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PlaylistList(
    songs: List<Song>,
    selectedSongsList: List<Song> = emptyList(),
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onSongClick: (song: Song) -> Unit = {},
    onSongLongClick: (song: Song) -> Unit = {},
    bottomPadding: Dp = 0.dp
) {
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            val fromIndex = from.index
            val toIndex = to.index
            if (fromIndex != toIndex &&
                fromIndex in songs.indices &&
                toIndex in songs.indices
            ) {
                onMove(fromIndex, toIndex)
                haptic.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
        }
    )
    if (songs.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.MusicOff,
                contentDescription = null,
                modifier = Modifier.padding(top = 70.dp).size(50.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "Your playlist is empty. To add songs, go to your library and long-press on a song to see the options and select 'Add to Playlist'.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = bottomPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        itemsIndexed(
            items = songs,
            key = { _, song -> song.localId ?: song.hashCode() },
        ) { index, song ->
            ReorderableItem(
                state = reorderableState,
                key = song.localId ?: song.hashCode(),
                modifier = Modifier.height(75.dp)
            ) { isDragging ->
                val interactionSource = remember { MutableInteractionSource() }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SongPosition(
                        index = index,
                        isDragging = isDragging,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .fillMaxHeight()
                            .padding(vertical = 1.dp)
                            .widthIn(min = 22.dp, max = 30.dp)
                    )
                    SongItem(
                        songTitle = song.title,
                        songArtist = song.artist,
                        isSelected = selectedSongsList.contains(song),
                        onSongClick = { onSongClick(song) },
                        onLongClick = { onSongLongClick(song) },
                        isDragging = isDragging,
                        trailingContent = {
                            Row {
                                val hapticFeedback = LocalHapticFeedback.current

                                IconButton(
                                    modifier = Modifier
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onLongPress = {
                                                    hapticFeedback.performHapticFeedback(
                                                        HapticFeedbackType.LongPress
                                                    )
                                                }
                                            )
                                        }
                                        .draggableHandle(
                                            interactionSource = interactionSource,
                                            onDragStarted = {
                                                hapticFeedback.performHapticFeedback(
                                                    HapticFeedbackType.GestureThresholdActivate
                                                )
                                            },
                                            onDragStopped = {
                                                hapticFeedback.performHapticFeedback(
                                                    HapticFeedbackType.GestureEnd
                                                )
                                            },
                                        )
                                        .clearAndSetSemantics { },
                                    onClick = {},
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.DragHandle,
                                            contentDescription = "Reorder"
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SongPosition(
    index: Int,
    isDragging: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isDragging) {
            MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)
        } else {
            MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        },
    )

    val textColor by animateColorAsState(
        targetValue = if (isDragging) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
    )

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
    )

    Surface(
        modifier = modifier
            .fillMaxHeight()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(40.dp)),
        color = backgroundColor,
        tonalElevation = 2.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (index + 1).toString(),
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    }
}