package com.chordbay.app.ui.composable.component.alertdialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.helper.pluralText
import com.chordbay.app.data.model.Song

@Composable
fun PrivacyBulkDialog(
    songs: List<Song>,
    onDismiss: () -> Unit,
    onApply: (defaultIsPublic: Boolean, overrides: Map<Int, Boolean>) -> Unit
) {
    var defaultIsPublic by remember { mutableStateOf(true) }
    val overrides = remember { mutableStateMapOf<Int, Boolean>() }

    fun setDefault(value: Boolean) {
        if (defaultIsPublic == value) return
        defaultIsPublic = value
        overrides.clear() // make all items follow the new default
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onApply(defaultIsPublic, overrides.toMap()) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Visibility for ${pluralText("song", songs.size)}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Default visibility")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = defaultIsPublic,
                        onClick = { setDefault(true) },
                        label = { Text("Public") }
                    )
                    FilterChip(
                        selected = !defaultIsPublic,
                        onClick = { setDefault(false) },
                        label = { Text("Private") }
                    )
                }

                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                Text("Override per item (optional)")
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(songs, key = { it.localId ?: it.hashCode() }) { song ->
                        val key = song.localId ?: return@items
                        val current = overrides[key] ?: defaultIsPublic
                        SongOverrideRow(
                            song = song,
                            isPublic = current,
                            onChange = { overrides[key] = it }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun SongOverrideRow(
    song: Song,
    isPublic: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildString {
                append(song.artist.ifBlank { "Unknown Artist" })
                append(" - ")
                append(song.title.ifBlank { "Untitled" })
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = { onChange(true) },
                label = { Text("Public") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isPublic) MaterialTheme.colorScheme.secondaryContainer else AssistChipDefaults.assistChipColors().containerColor
                )
            )
            AssistChip(
                onClick = { onChange(false) },
                label = { Text("Private") },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (!isPublic) MaterialTheme.colorScheme.secondaryContainer else AssistChipDefaults.assistChipColors().containerColor
                )
            )
        }
    }
}
