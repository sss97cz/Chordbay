package com.chordbay.app.ui.composable.component.button

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chordbay.app.data.model.chord.Chords.Companion.transposeChord
import com.chordbay.app.data.model.chord.HBFormat
import com.chordbay.app.ui.theme.imagevector.Check_indeterminate_small

@Composable
fun TransposeButton(
    modifier: Modifier = Modifier,
    initialSemitones: Int,
    initialChord: String,
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    hBFormat: HBFormat = HBFormat.ENG,
    songHBFormat: HBFormat
) {
    var currentSemitones by rememberSaveable(initialSemitones) { mutableIntStateOf(initialSemitones) }
    var currentChordString by rememberSaveable(initialChord, initialSemitones, hBFormat, songHBFormat) {
        mutableStateOf(initialChord.transposeChord(initialSemitones, hBFormat, songHBFormat))
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            IconButton(
                onClick = {
                    currentSemitones--
                    onDownClick()
                    currentChordString = initialChord.transposeChord(currentSemitones, hBFormat, songHBFormat)

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
                text = currentChordString,
            )
            IconButton(
                onClick = {
                    currentSemitones++
                    onUpClick()
                    currentChordString = initialChord.transposeChord(currentSemitones, hBFormat, songHBFormat)
                },
                modifier = Modifier.size(25.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Transpose Up",
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
