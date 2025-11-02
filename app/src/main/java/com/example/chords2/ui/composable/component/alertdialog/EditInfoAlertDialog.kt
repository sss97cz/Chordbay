package com.example.chords2.ui.composable.component.alertdialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun EditInfoAlertDialog(
    onDismissRequest: () -> Unit,
    showAlertDialog: Boolean
) {
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = "Info") },
            text = {
                Text(
                    """
                    To make chord transposition work, place chords inside square brackets.
                    
                    Example:
                    [C]Loren [Am]ipsum...
                    
                    Chords written without brackets won’t be detected:
                    C Loren Am ipsum...
                    
                    When sharing songs, please make sure the chords are properly formatted with brackets to ensure a better experience for you and other users.
                    """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = onDismissRequest) { Text("Got it") }
            },
        )
    }
}