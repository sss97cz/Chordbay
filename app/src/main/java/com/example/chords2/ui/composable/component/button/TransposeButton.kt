package com.example.chords2.ui.composable.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chords2.data.model.Chords
import com.example.chords2.data.model.Chords.Companion.transposeChord
import com.example.chords2.ui.composable.Check_indeterminate_small

@Composable
fun TransposeButton(
    modifier: Modifier = Modifier,
    initialSemitones: Int,
    initialChord: String
) {
    var currentChordString by remember { mutableStateOf(initialChord) }
    var currentSemitones by remember { mutableIntStateOf(initialSemitones) }
    LaunchedEffect(initialChord, initialSemitones) {
        currentSemitones = initialSemitones
        currentChordString = initialChord.transposeChord(currentSemitones)
    }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(100.dp, 40.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(2.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    currentSemitones--
                    currentChordString = initialChord.transposeChord(currentSemitones)

                },
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    Check_indeterminate_small,
                    contentDescription = "Transpose Down",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Text(
                text = currentChordString
            )
            IconButton(
                onClick = {
                    currentSemitones++
                    currentChordString = initialChord.transposeChord(currentSemitones)
                },
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Transpose Up",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Red
                )
            }
        }
    }
}
