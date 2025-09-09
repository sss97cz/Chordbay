package com.example.chords2.ui.composable.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SongContentEditor(
    modifier: Modifier = Modifier,
    onValueChange: (TextFieldValue) -> Unit,
    value: TextFieldValue,
) {
// Needed for auto-scrolling when keyboard pushes things up
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    // State for focus handling
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding() // ensures padding above keyboard
    ) {
        // Scrollable container
        val scrollState = rememberScrollState()

        BasicTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            onTextLayout = { layoutResult ->
                // Auto-scroll when cursor moves
                // Use the actual cursor position, not text.length
                val cursorOffset = value.selection.start
                val cursorRect = layoutResult.getCursorRect(cursorOffset)
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView(cursorRect)
                }
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
                .verticalScroll(scrollState)
                .padding(16.dp)
                .bringIntoViewRequester(bringIntoViewRequester),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            )
        ) { innerTextField ->
            if (value.text.isEmpty()) {
                Text(
                    text = "Type your song here...",
                )
            }
            innerTextField()
        }
    }
}