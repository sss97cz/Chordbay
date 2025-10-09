package com.example.chords2.ui.composable.component.text

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.chords2.data.model.util.Chords
import com.example.chords2.data.model.util.Chords.Companion.transpose

@Composable
fun SongText(
    modifier: Modifier,
    text: String,
    fontSize: Int,
    semitones: Int,
    chordsColor: Color = Color.Unspecified
) {
    Text(
        text = text.highlightChords(semitones = semitones, chordsColor = chordsColor, fontSize = fontSize),
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.6f).sp
    )
}

private fun String.highlightChords(
    semitones: Int,
    fontSize: Int,
    chordsColor: Color = Color.Unspecified
): AnnotatedString {
    val allChords = Chords.allChordsToString()
    Log.d("SongText", "highlightChords: $allChords")

    var isBuildingChord = false
    var chordContent = ""
    return buildAnnotatedString {
        this@highlightChords.forEachIndexed { index, char ->
            when (char) {
                '[' -> {
                    isBuildingChord = true
                    chordContent = ""
                }

                ']' -> {
                    isBuildingChord = false
                    val isChord = chordContent in allChords
                    if (isChord) {
                        withStyle(
                            SpanStyle(
                                color = chordsColor,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                fontSize = (fontSize * 1.3f).sp,
                                baselineShift = BaselineShift(0.5f),
                            )
                        ) {
                            val baseChord = Chords.allBaseChords.firstOrNull {
                                chordContent.contains(it.value)
                            }
                            val suffix = chordContent.substringAfter(baseChord?.value ?: "")
                            val transposedBaseChord =
                                baseChord?.transpose(semitones)?.value ?: "error"
                            append(transposedBaseChord + suffix)
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = (fontSize * 0.25f).sp,
                            )
                        ) {
                            append(" ") // space for better readability
                        }
                    } else {
                        append("[$chordContent]") // if not a chord, keep it literal
                    }
                }

                else -> {
                    if (isBuildingChord) {
                        chordContent += char
                    } else {
                        append(char)
                    }
                }
            }
        }
    }
}
