package com.chordbay.app.ui.composable.component.text

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.chordbay.app.data.model.chord.Chords
import com.chordbay.app.data.model.chord.Chords.A.mapBaseChord
import com.chordbay.app.data.model.chord.Chords.Companion.transpose
import com.chordbay.app.data.model.chord.HBFormat
import com.chordbay.app.ui.composable.fredboard.GuitarChord

//@Composable
//fun SongText(
//    modifier: Modifier,
//    text: String,
//    fontSize: Int,
//    semitones: Int,
//    chordsColor: Color = Color.Unspecified,
//    hBFormat: HBFormat = HBFormat.GER,
//    hbFormatSong: HBFormat = HBFormat.GER,
//) {
//    Text(
//        text = text.highlightChords(
//            semitones = semitones,
//            chordsColor = chordsColor,
//            fontSize = fontSize,
//            hBFormat = hBFormat,
//            hbFormatSong = hbFormatSong,
//        ),
//        modifier = modifier,
//        fontFamily = FontFamily.Monospace,
//        fontSize = fontSize.sp,
//        lineHeight = (fontSize * 1.6f).sp
//    )
//}
private const val CHORD_TAG = "chord_tag"

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
    val annotated = remember(text, fontSize, semitones, chordsColor, hBFormat, hbFormatSong) {
        text.highlightChords(
            semitones = semitones,
            chordsColor = chordsColor,
            fontSize = fontSize,
            hBFormat = hBFormat,
            hbFormatSong = hbFormatSong,
        )
    }

    var popupChord by remember { mutableStateOf<String?>(null) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val density = LocalDensity.current

    Box(modifier = modifier) {
        Text(
            text = text.highlightChords(
                semitones = semitones,
                chordsColor = chordsColor,
                fontSize = fontSize,
                hBFormat = hBFormat,
                hbFormatSong = hbFormatSong,
            ),
            modifier = modifier.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        Log.d("SongText", "onLongPress at offset: $offset")
                        popupOffset = offset
                        val layout = textLayoutResult ?: return@detectTapGestures
                        val index = layout.getOffsetForPosition(offset)
                        val position = annotated.getStringAnnotations(
                            tag = CHORD_TAG,
                            start = if (index - 1 >= 0 ) index - 1 else index,
                            end = index + 1
                        ).firstOrNull()
                        popupChord = position?.item
                        Log.d("SongText", "Found chord at index $index: ${popupChord ?: "none"}" )
                    }
                )
            },
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.6f).sp,
            onTextLayout = { layoutResult ->
                textLayoutResult = layoutResult
            }
        )

        if (popupChord != null) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(popupOffset.x.toInt(), popupOffset.y.toInt()),
                onDismissRequest = { popupChord = null }
            ) {
                Surface {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = popupChord ?: "",
                            )
                            GuitarChord(
                                fingering = "2|3|2|0|x|x",
                                fromFret = 0,
                                toFret = 4,
                            )
                        }
                    }
                }
            }
        }
    }
}

//private fun String.highlightChords(
//    semitones: Int,
//    fontSize: Int,
//    chordsColor: Color = Color.Unspecified,
//    hBFormat: HBFormat,
//    hbFormatSong: HBFormat,
//): AnnotatedString {
//    val allChords = Chords.allChordsToString(hbFormatSong)
//    Log.d("SongText", "highlightChords: $allChords")
//
//    var isBuildingChord = false
//    var chordContent = ""
//    return buildAnnotatedString {
//        this@highlightChords.forEachIndexed { index, char ->
//            when (char) {
//                '[' -> {
//                    isBuildingChord = true
//                    chordContent = ""
//                }
//
//                ']' -> {
//                    isBuildingChord = false
//                    val isChord = chordContent in allChords
//                    if (isChord) {
//                        withStyle(
//                            SpanStyle(
//                                color = chordsColor,
//                                fontWeight = FontWeight.Bold,
//                                fontStyle = FontStyle.Italic,
//                                fontSize = (fontSize * 1.3f).sp,
//                                baselineShift = BaselineShift(0.5f),
//                            )
//                        ) {
//                            val baseChord = Chords.getBaseChordsList(hbFormatSong)
//                                .sortedByDescending { it.value.length }
//                                .firstOrNull { chordContent.startsWith(it.value) }
//                            val suffix = chordContent.substringAfter(baseChord?.value ?: "")
//                            val transposedBaseChord =
//                                baseChord?.transpose(semitones, hBFormat)?.mapBaseChord(
//                                    userFormat = hBFormat,
//                                    songFormat = hbFormatSong
//                                ) ?: "error"
//                            append(transposedBaseChord + suffix)
//                        }
//                        withStyle(
//                            SpanStyle(
//                                fontSize = (fontSize * 0.25f).sp,
//                            )
//                        ) {
//                            append(" ") // space for better readability
//                        }
//                    } else {
//                        append("[$chordContent]")
//                    }
//                }
//
//                else -> {
//                    if (isBuildingChord) {
//                        chordContent += char
//                    } else {
//                        append(char)
//                    }
//                }
//            }
//        }
//    }
//}
private fun String.highlightChords(
    semitones: Int,
    fontSize: Int,
    chordsColor: Color = Color.Unspecified,
    hBFormat: HBFormat,
    hbFormatSong: HBFormat,
): AnnotatedString {
    val allChords = Chords.allChordsToString(hbFormatSong)

    var isBuildingChord = false
    var chordContent = ""
    return buildAnnotatedString {
        this@highlightChords.forEachIndexed { _, char ->
            when (char) {
                '[' -> {
                    isBuildingChord = true
                    chordContent = ""
                }

                ']' -> {
                    isBuildingChord = false
                    val isChord = chordContent in allChords
                    if (isChord) {
                        val baseChord = Chords.getBaseChordsList(hbFormatSong)
                            .sortedByDescending { it.value.length }
                            .firstOrNull { chordContent.startsWith(it.value) }
                        val suffix = chordContent.substringAfter(baseChord?.value ?: "")
                        val transposedBaseChord =
                            baseChord?.transpose(semitones, hBFormat)?.mapBaseChord(
                                userFormat = hBFormat,
                                songFormat = hbFormatSong
                            ) ?: "error"
                        val finalText = transposedBaseChord + suffix

                        // annotate this chord
                        pushStringAnnotation(
                            tag = CHORD_TAG,
                            annotation = finalText
                        )
                        withStyle(
                            SpanStyle(
                                color = chordsColor,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                fontSize = (fontSize * 1.3f).sp,
                                baselineShift = BaselineShift(0.5f),
                            )
                        ) {
                            append(finalText)
                        }
                        pop() // end CHORD_TAG

                        withStyle(
                            SpanStyle(
                                fontSize = (fontSize * 0.25f).sp,
                            )
                        ) {
                            append(" ")
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