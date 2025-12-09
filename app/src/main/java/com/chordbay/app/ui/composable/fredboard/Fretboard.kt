package com.chordbay.app.ui.composable.fredboard
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chordbay.app.data.model.chord.Chords
import com.chordbay.app.data.model.chord.Chords.A.mapBaseChord
import com.chordbay.app.data.model.chord.Chords.Companion.toOrdinal
import com.chordbay.app.data.model.chord.HBFormat

@Composable
fun GuitarChord(
    fingering: String,
    fromFret: Int = -1,
    toFret: Int = -1,
    scale: Float = 1.5f,
    hbFormat: HBFormat,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    if (fromFret > 24 || toFret > 25) {
        throw IllegalArgumentException("Fret range out of bounds")
    }
    // Helper to parse string to markers (implementation from snippet)
    val markers = fingering.fingering.map {
        if (it is FretboardMarker.FrettedNote) it.copy(hbFormat = hbFormat) else it
    }
    val from = if (fromFret < 0) {
        fingering.filter { it != 'x' && it != '|' }
            .mapNotNull { it.toString().toIntOrNull() }
            .minOrNull() ?: 0
        }
    else fromFret
    var to = if (toFret < 0) {
        fingering.filter { it != 'x' && it != '|' }
            .mapNotNull { it.toString().toIntOrNull() }
            .maxOrNull()?.plus(1) ?: 12
        }
    else toFret
    Log.d("GuitarChord", "Computed fret range: from $from to $to")
    if (to in 1..3) {
        to = 4 // Ensure at least 2 frets shown beyond open strings
    }
    GuitarChord2(markers, from, to, scale, hbFormat, onFretboardPressed)
}

@Composable
fun GuitarChord2(
    fretboardMarkers: List<FretboardMarker>,
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 25) toFret: Int = 12,
    scale: Float = 1.5f,
    hbFormat: HBFormat,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    Fretboard(fromFret, toFret, fretboardMarkers, scale, hbFormat, onFretboardPressed)
}

@Composable
fun Fretboard(
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 25) toFret: Int = 12,
    fretboardMarkers: List<FretboardMarker> = listOf(),
    scale: Float = 1.5f,
    hbFormat: HBFormat = HBFormat.ENG,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    check(fromFret in 0 until toFret) {
        "Invalid fret range"
    }

    val from = if (fromFret > 0) fromFret - 1 else fromFret
    val fretRange = toFret - from
    val totalWidth = (BASE_NUT_WIDTH + (BASE_FRET_WIDTH * fretRange)) * scale
    val totalHeight = BASE_FRETBOARD_HEIGHT * scale

    Column {
        Box(
            modifier = Modifier
                .size(width = totalWidth.dp, height = totalHeight.dp)
                .background(MaterialTheme.colorScheme.surface)
                .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurface)
        ) {
            // Layer 1: Frets and Nut (Background)
            Row(modifier = Modifier.fillMaxWidth()) {
                for (n in from until toFret) {
                    if (n == 0) {
                        Nut(scale)
                    } else {
                        Fretwire(modifier = Modifier.weight(1f), scale = scale)
                    }
                }
            }

            // Layer 2: Strings
            // Reversed: String 1 (High E) at top, String 6 (Low E) at bottom
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(6) { i ->
                    // i=0 -> String 1 (Thin)
                    // i=5 -> String 6 (Thick)
                    val stringIndex = i + 1

                    // Thickness logic: String 1 is thinnest (start at 1dp), String 6 thickest
                    val baseThickness = 1 + (i * 0.8) // 1, 1.8, 2.6, 3.4, 4.2, 5
                    val thicknessDp = (baseThickness * scale).coerceAtLeast(1.0).dp

                    GuitarString(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = (BASE_STRING_LEFT_PADDING * scale).dp),
                        thickness = thicknessDp
                    )
                }
            }

            // Layer 3: Markers (Interactive)
            FretMarkerLayer(from, toFret, fretboardMarkers, scale, hbFormat, onFretboardPressed)
        }

        // Fret Numbers
        FretNumberGutter(from, toFret, scale)
    }
}

@Composable
private fun FretMarkerLayer(
    fromFret: Int,
    toFret: Int,
    fretboardMarkers: List<FretboardMarker>,
    scale: Float = 1.5f,
    hbFormat: HBFormat,
    onFretboardPressed: (string: Int, fret: Int) -> Unit
) {
    Column(Modifier.fillMaxHeight()) {
        repeat(6) { i ->
            // String 1 (High E) at top (index 0), String 6 at bottom (index 5)
            val stringNumber = i + 1

            Row(Modifier.weight(1f)) {
                for (fretNumber in fromFret until toFret) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onFretboardPressed(stringNumber, fretNumber) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fretNumber == fromFret && fromFret == 0) {
                            // This is the Nut/Open string position
                            val openNote = fretboardMarkers.findOpenStringOrNull(stringNumber)
                            val mute = fretboardMarkers.findMutedStringOrNull(stringNumber)

                            // Adjust alignment for Nut area to be centered in the nut column
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = (BASE_NUT_WIDTH * scale).dp), // Pull back to align with nut
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                when {
                                    openNote != null -> FretMarker(openNote, scale)
                                    mute != null -> MutedMarker(scale)
                                    else -> {} // Empty
                                }
                            }
                        } else {
                            // Regular Fret
                            val marker = fretboardMarkers.findFrettedNoteOrNull(stringNumber, fretNumber)
                            FretMarker(marker, scale)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuitarString(modifier: Modifier, thickness: Dp) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thickness)
                .background(
                    brush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
private fun Fretwire(modifier: Modifier, scale: Float) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        Box(
            modifier = Modifier
                .size(
                    width = (BASE_FRETWIRE_WIDTH * scale).dp,
                    height = (BASE_FRETBOARD_HEIGHT * scale).dp
                )
                .background(
                    brush = SolidColor(MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
private fun Nut(scale: Float) {
    Row {
        // Area behind nut
        Box(
            modifier = Modifier
                .size(
                    width = (BASE_NUT_COLUMN_WIDTH * scale).dp,
                    height = (BASE_FRETBOARD_HEIGHT * scale).dp
                )
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        // The Nut itself
        Box(
            modifier = Modifier
                .size(
                    width = (BASE_NUT_WIDTH * scale).dp,
                    height = (BASE_FRETBOARD_HEIGHT * scale).dp
                )
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}

@Composable
private fun MutedMarker(scale: Float) {
    // Centered X
    Box(
        modifier = Modifier
            .size((BASE_FRETMARKER_SIZE * scale).dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "X",
            style = MaterialTheme.typography.labelSmall,
            fontSize = (BASE_FRETMARKER_SIZE * scale).sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun FretMarker(marker: FretboardMarker.FrettedNote?, scale: Float) {
    if (marker != null && marker.pitch != null) {
        Box(
            modifier = Modifier
                .size((BASE_FRETMARKER_SIZE * scale).dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            val text = marker.pitch.mapBaseChord(
                userFormat = marker.hbFormat,
                songFormat = marker.hbFormat
            ) ?: marker.pitch.value

            Text(
                text = text.replace("s", "#"),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                fontSize = (BASE_FRETMARKER_SIZE * scale * 0.6f).sp, // Slightly smaller to fit in circle
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = (1 * scale).dp) // Visual centering adjustment
            )
        }
    }
}

@Composable
private fun FretNumberGutter(fromFret: Int, toFret: Int, scale: Float) {
    val fretRange = toFret - fromFret
    Box(
        modifier = Modifier
            .size(
                width = (BASE_FRET_WIDTH * fretRange * scale).dp,
                height = (BASE_FRET_BOARD_GUTTER_HEIGHT * scale).dp
            )
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (n in fromFret until toFret) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = (BASE_FRET_NUMBER_GUTTER_PADDING_RIGHT * scale).dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (n > 0 && n != fromFret) {
                        Text(
                            text = "$n",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = (10 * scale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
// Constants
private const val BASE_FRETMARKER_CONTAINER_HEIGHT = 16
private const val BASE_FRETBOARD_HEIGHT = 6 * BASE_FRETMARKER_CONTAINER_HEIGHT
private const val BASE_FRET_WIDTH = 24
private const val BASE_STRING_LEFT_PADDING = 10
private const val BASE_FRETMARKER_SIZE = 16 // Increased slightly for better fit
private const val BASE_NUT_COLUMN_WIDTH = 12
private const val BASE_NUT_WIDTH = 4
private const val BASE_FRETWIRE_WIDTH = 2
private const val BASE_FRET_BOARD_GUTTER_HEIGHT = 16
private const val BASE_FRET_NUMBER_GUTTER_PADDING_RIGHT = 4
sealed class FretboardMarker {

    data class FrettedNote(
        @IntRange(from = 1)
        val stringNumber: Int,
        @IntRange(from = 0)
        val fretNumber: Int,
        val hbFormat: HBFormat = HBFormat.ENG,
        val tuning: (stringNumber: Int) -> Chords = standardTuningSixString(hbFormat),
        val pitch: Chords? = run {
            if (fretNumber < 0) {
                null
            } else {
                val openStringChord = tuning(stringNumber)
                val openOrdinal = openStringChord.toOrdinal(hbFormat)
                val newIndex = (fretNumber + openOrdinal) % 12
                Chords.getBaseChordsList(hbFormat)[newIndex]
            }
        }
    ) : FretboardMarker()

    data class MutedString(
        @IntRange(from = 1)
        val stringNumber: Int
    ) : FretboardMarker()
}
fun standardTuningSixString(hbFormat: HBFormat): (stringNumber: Int) -> Chords = { stringNumber ->
    when (stringNumber) {
        1 -> Chords.E
        2 -> if (hbFormat == HBFormat.GER) Chords.H else Chords.BEng
        3 -> Chords.G
        4 -> Chords.D
        5 -> Chords.A
        6 -> Chords.E
        else -> Chords.E
    }
}

fun List<FretboardMarker>.findOpenStringOrNull(stringNumber: Int): FretboardMarker.FrettedNote? = firstOrNull {
    it is FretboardMarker.FrettedNote && it.stringNumber == stringNumber && it.fretNumber == 0
} as? FretboardMarker.FrettedNote

fun List<FretboardMarker>.findMutedStringOrNull(stringNumber: Int) =
    firstOrNull { it is FretboardMarker.MutedString && it.stringNumber == stringNumber } as? FretboardMarker.MutedString

fun List<FretboardMarker>.findFrettedNoteOrNull(stringNumber: Int, fretNumber: Int): FretboardMarker.FrettedNote? =
    firstOrNull {
        it is FretboardMarker.FrettedNote && it.stringNumber == stringNumber && it.fretNumber == fretNumber
    } as? FretboardMarker.FrettedNote

val String.fingering: List<FretboardMarker>
    get() {
        val markers: List<FretboardMarker?> = split("|").mapIndexed { index, value ->
            when {
                value == "x" -> FretboardMarker.MutedString(index + 1)
                value.toIntOrNull() != null -> FretboardMarker.FrettedNote(
                    index + 1, value.toInt(),
                )
                value == "" -> null
                else -> throw IllegalArgumentException("Invalid fingering format $value")
            }
        }

        return markers.filterNotNull()
    }

val List<FretboardMarker>.encodeFingering: String
    get() {
        return joinToString(separator = "|") {
            when (it) {
                is FretboardMarker.FrettedNote -> it.fretNumber.toString()
                is FretboardMarker.MutedString -> "x"
            }
        }
    }

val FretboardMarker.extractStringNumber: Int
    get() {
        return when (this) {
            is FretboardMarker.FrettedNote -> stringNumber
            is FretboardMarker.MutedString -> stringNumber
        }
    }


