package com.example.chords2.ui.composable.component

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.chords2.data.model.Chords
import com.example.chords2.data.model.Chords.Companion.transpose

@Composable
fun SongText(
    modifier: Modifier,
    text: String,
    semitones: Int,
) {
    Text(
        text = text.highlightChords(semitones = semitones),
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
    )
}

private fun String.highlightChords(semitones: Int): AnnotatedString {
    val allChords = Chords.allChordsToString()
    Log.d("SongText", "highlightChords: $allChords")

    var isBuildingChord = false
    var chordContent = ""
    return buildAnnotatedString {
        this@highlightChords.forEachIndexed { index, char ->
            if (char == '[') {
                isBuildingChord = true
            }
            if (!isBuildingChord && chordContent.isEmpty()) {
                append(char)
            }
            if (char == ']') {
                isBuildingChord = false
                val chordContentNoBrackets = chordContent.substring(1,chordContent.length)
                if (chordContentNoBrackets in allChords) {
                    withStyle(
                        SpanStyle(
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                    ) {
                        val baseChord = Chords.allBaseChords.firstOrNull{
                            chordContentNoBrackets.contains(it.value)
                        }
                        val sufix = chordContentNoBrackets.substringAfter(baseChord?.value ?: "")
                        append((baseChord?.transpose(semitones)?.value ?: "error") + sufix)
                    }
                    chordContent = ""
                } else {
                    chordContent += char
                    append(chordContent)
                    chordContent = ""
                }
            }
            if (isBuildingChord) {
                chordContent += char
            }
        }
    }

}
