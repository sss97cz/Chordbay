package com.chordbay.app.ui.composable.component.textfield

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SongTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    value: String,
    label: String = "",
    singleLine: Boolean = false
){
    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = {
            Text(text = label)
        },
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        singleLine = singleLine,
    )
}



