package com.example.chords2.ui.composable.component.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetContent(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPostClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onEditClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit" )
        }
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete" )
        }
        IconButton(
            onClick = onPostClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = "Post" )
        }
    }
}