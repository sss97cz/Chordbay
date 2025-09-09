package com.example.chords2.ui.composable.component.textfield

import android.R.attr.singleLine
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SongTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    value: String,
    label: String = "",
    singleLine: Boolean = false
){
    TextField(
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



