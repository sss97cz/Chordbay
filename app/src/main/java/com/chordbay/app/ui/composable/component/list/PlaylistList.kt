package com.chordbay.app.ui.composable.component.list
//
//
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.filled.KeyboardArrowUp
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.chordbay.app.data.model.Song
//import com.chordbay.app.ui.composable.component.listitem.SongItem
//
//@Composable
//fun PlaylistList(
//    songs: List<Song>,
//    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
//    onDelete: (index: Int) -> Unit,
//    onSongClick: (song: Song) -> Unit = {},
//    onSongLongClick: (song: Song) -> Unit = {}
//) {
//    LazyColumn {
//        itemsIndexed(songs) { index, song ->
//            SongItem(
//                songTitle = song.title,
//                songArtist = song.artist,
//                isSelected = false,
//                onSongClick = { onSongClick(song) },
//                onLongClick = { onSongLongClick(song) },
//                trailingContent = {
//                    Row {
//                        IconButton(
//                            onClick = { if (index > 0) onMove(index, index - 1) },
//                            enabled = index > 0
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.KeyboardArrowUp,
//                                contentDescription = "Move up",
//                                modifier = Modifier.size(20.dp)
//                            )
//                        }
//                        IconButton(
//                            onClick = { if (index < songs.size - 1) onMove(index, index + 1) },
//                            enabled = index < songs.size - 1
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.KeyboardArrowDown,
//                                contentDescription = "Move down",
//                                modifier = Modifier.size(20.dp)
//                            )
//                        }
//                        IconButton(onClick = { onDelete(index) }) {
//                            Icon(
//                                imageVector = Icons.Filled.Delete,
//                                contentDescription = "Delete",
//                                modifier = Modifier.size(20.dp)
//                            )
//                        }
//                    }
//                }
//            )
//        }
//    }
//}
//
//
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.model.Song
import com.chordbay.app.data.model.chord.Chords
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

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
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
                modifier = Modifier.heightIn(min = 70.dp)
            ) { isDragging ->
                val interactionSource = remember { MutableInteractionSource() }
                SongItem(
                    songTitle = song.title,
                    songArtist = song.artist,
                    isSelected = selectedSongsList.contains(song),
                    onSongClick = { onSongClick(song) },
                    onLongClick = { onSongLongClick(song) },
                    isDragging = isDragging,
                    trailingContent = {
                        Row {
                            // drag handle – drag starts only on **long press**
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
                                    .clearAndSetSemantics { }, // avoid double semantics for a11y
                                onClick = { /* no-op: this is just a handle */ },
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