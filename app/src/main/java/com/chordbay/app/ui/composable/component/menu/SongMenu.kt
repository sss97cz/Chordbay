package com.chordbay.app.ui.composable.component.menu

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
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

