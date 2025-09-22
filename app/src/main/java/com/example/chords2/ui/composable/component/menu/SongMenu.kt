package com.example.chords2.ui.composable.component.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPostClick: () -> Unit,
    scope: CoroutineScope,
    sheetState: SheetState
) {
//    DropdownMenu(
//        expanded = expanded,
//        onDismissRequest = onDismissRequest,
//        offset = offset
//    ) {
//        DropdownMenuItem(
//            text = { Text("Edit", style = MaterialTheme.typography.bodyMedium) },
//            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Edit") },
//            onClick = {
//                onEditClick()
//                onDismissRequest()
//            }
//        )
//        DropdownMenuItem(
//            text = { Text("Delete", style = MaterialTheme.typography.bodyMedium) },
//            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "Delete") },
//            onClick = {
//                onDeleteClick()
//                onDismissRequest()
//            }
//        )
//        DropdownMenuItem(
//            text = { Text("Post", style = MaterialTheme.typography.bodyMedium) },
//            leadingIcon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Post") },
//            onClick = {
//                onPostClick()
//                onDismissRequest()
//            }
//        )
//    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        // Sheet content
        Button(onClick = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest
                }
            }
        }) {
            Text("Hide bottom sheet")
        }
    }
}

