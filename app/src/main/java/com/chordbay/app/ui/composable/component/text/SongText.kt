package com.chordbay.app.ui.composable.component.text

import android.util.Log
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
import com.chordbay.app.data.model.chord.Chords
import com.chordbay.app.data.model.chord.Chords.A.mapBaseChord
import com.chordbay.app.data.model.chord.Chords.Companion.transpose
import com.chordbay.app.data.model.chord.HBFormat

@Composable
fun SongText(
    modifier: Modifier,
    text: String,
    fontSize: Int,
    semitones: Int,
    chordsColor: Color = Color.Unspecified,
    hBFormat: HBFormat = HBFormat.GER,
    hbFormatSong: HBFormat = HBFormat.GER,
) {
    Text(
        text = text.highlightChords(
            semitones = semitones,
            chordsColor = chordsColor,
            fontSize = fontSize,
            hBFormat = hBFormat,
            hbFormatSong = hbFormatSong,
        ),
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.6f).sp
    )
}

private fun String.highlightChords(
    semitones: Int,
    fontSize: Int,
    chordsColor: Color = Color.Unspecified,
    hBFormat: HBFormat,
    hbFormatSong: HBFormat,
): AnnotatedString {
    val allChords = Chords.allChordsToString(hbFormatSong)
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
                            val baseChord = Chords.getBaseChordsList(hbFormatSong)
                                .sortedByDescending { it.value.length }
                                .firstOrNull { chordContent.startsWith(it.value) }
                            val suffix = chordContent.substringAfter(baseChord?.value ?: "")
                            val transposedBaseChord =
                                baseChord?.transpose(semitones, hBFormat)?.mapBaseChord(
                                    userFormat = hBFormat,
                                    songFormat = hbFormatSong
                                ) ?: "error"
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
                        append("[$chordContent]")
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
