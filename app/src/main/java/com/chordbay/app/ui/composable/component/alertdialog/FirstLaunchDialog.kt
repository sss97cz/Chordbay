package com.chordbay.app.ui.composable.component.alertdialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FirstLaunchDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.heightIn(max = 600.dp),
        onDismissRequest = onDismiss,
        title = {
            Text("Welcome to ChordBay!")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    """
                                Welcome to ChordBay Songbook!
        
                                ChordBay is an open-source app for organizing, editing, and viewing songs with chords and lyrics.
        
                                You can create your own songs using the built-in editor, or:
                                • Import songs in .txt format from your device
                                • Download public songs from the Browse tab
        
                                By creating an account, you can share your songs with other users. 
                                Simply long-press a song in your library to upload it as:
                                • Private — visible only to you
                                • Public — downloadable by others
        
                                When logged in, your uploaded songs are automatically synced across all your devices.
        
                                If you enjoy using ChordBay, consider supporting its development on Ko-fi or contributing on GitHub.
                                Happy playing!
                        """.trimIndent()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("OK")
            }
        },
    )
}