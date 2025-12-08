package com.chordbay.app.ui.composable.fredboard
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes

import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun GuitarChord(
    fingering: String,
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 25) toFret: Int = 12,
    scale: Float = 1.5f,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    GuitarChord(fingering.fingering, fromFret, toFret, scale, onFretboardPressed)
}

@Composable
fun GuitarChord(
    fretboardMarkers: List<FretboardMarker>,
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 25) toFret: Int = 12,
    scale: Float = 1.5f,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    Fretboard(fromFret, toFret, fretboardMarkers, scale, onFretboardPressed)
}

@Composable
fun Fretboard(
    @IntRange(from = 0, to = 24) fromFret: Int = 0,
    @IntRange(from = 0, to = 25) toFret: Int = 12,
    fretboardMarkers: List<FretboardMarker> = listOf(),
    scale: Float = 1.5f,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    check(fromFret in 0 until toFret) {
        "Invalid fret range"
    }

    val from = if (fromFret > 0) fromFret - 1 else fromFret
    val fretRange = toFret - from

    Column {
        Box(
            modifier = Modifier
                .size(
                    width = (BASE_FRET_WIDTH * scale * fretRange).dp,
                    height = (BASE_FRETBOARD_HEIGHT * scale).dp
                )
                .background(Color.White)
                .border(width = 1.dp, color = Color.Black)
        ) {
            // Layer 1: Frets and Nut
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
            Column(modifier = Modifier.fillMaxWidth()) {
                repeat(6) { index ->
                    GuitarString(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = (BASE_STRING_LEFT_PADDING * scale).dp),
                        thickness = ((((BASE_GUITAR_STRING_THICKNESS - index) * scale).toInt()
                            .let { if (it < 3) 3 else it }).dp)
                    )
                }
            }

            // Layer 3: Markers
            FretMarkerLayer(from, toFret, fretboardMarkers, scale, onFretboardPressed)
        }
        FretNumberGutter(from, toFret, scale)
    }
}

@Composable
private fun FretNumberGutter(fromFret: Int, toFret: Int, scale: Float = 1.5f) {
    val fretRange = toFret - fromFret
    Box(
        modifier = Modifier
            .size(
                width = (BASE_FRET_WIDTH * fretRange * scale).dp,
                height = (BASE_FRET_BOARD_GUTTER_HEIGHT * scale).dp
            )
            .background(Color.White)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (n in fromFret until toFret) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = (BASE_FRET_NUMBER_GUTTER_PADDING_RIGHT * scale).dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (n <= 0 || n != fromFret) {
                        Text(text = "$n")
                    }
                }
            }
        }
    }
}

@Composable
private fun FretMarkerLayer(
    fromFret: Int,
    toFret: Int,
    fretboardMarkers: List<FretboardMarker>,
    scale: Float = 1.5f,
    onFretboardPressed: (string: Int, fret: Int) -> Unit = { _, _ -> }
) {
    // Replaced legacy Table with Column of Rows
    Column(Modifier.fillMaxHeight()) {
        repeat(6) { index ->
            val stringNumber = 6 - index
            Row(Modifier.weight(1f)) {
                for (fretNumber in fromFret until toFret) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(onClick = {
                                onFretboardPressed(
                                    stringNumber,
                                    fretNumber
                                )
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fretNumber == fromFret) {
                            val openNote = fretboardMarkers.findOpenStringOrNull(stringNumber)
                            val mute = fretboardMarkers.findMutedStringOrNull(stringNumber)
                            when {
                                openNote != null -> FretMarker(openNote, scale)
                                mute != null -> MutedMarker(mute, scale)
                                else -> FretMarker(null, scale)
                            }
                        } else {
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
private fun GuitarString(modifier: Modifier, thickness: Dp = 3.dp) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thickness)
                .background(
                    brush = SolidColor(Color.LightGray),
                    shape = RoundedCornerShape(1.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
private fun Fretwire(modifier: Modifier, scale: Float = 1.5f) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        Box(
            modifier = Modifier
                .size(
                    width = (BASE_FRETWIRE_WIDTH * scale).dp,
                    height = (BASE_FRETBOARD_HEIGHT * scale).dp
                )
                .background(
                    brush = SolidColor(Color.Gray),
                    shape = RoundedCornerShape(1.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
private fun Nut(scale: Float = 1.5f) {
    Row {
        Box(
            modifier = Modifier
                .size(
                    width = (BASE_NUT_COLUMN_WIDTH * scale).dp,
                    height = (BASE_FRETBOARD_HEIGHT * scale).dp
                )
                .background(brush = SolidColor(Color.Gray))
        )
        Box(
            modifier = Modifier
                .size(
                    width = (BASE_NUT_WIDTH * scale).dp,
                    height = (BASE_FRETBOARD_HEIGHT * scale).dp
                )
                .background(brush = SolidColor(Color.Black))
        )
    }
}

@Composable
private fun MutedMarker(marker: FretboardMarker.MutedString, scale: Float = 1.5f) {
    Box(
        modifier = Modifier
            .size(
                width = (BASE_FRET_WIDTH * scale).dp,
                height = (BASE_FRETMARKER_CONTAINER_HEIGHT * scale).dp
            )
            .padding(
                end = (BASE_MUTED_MARKER_RIGHT_PADDING * scale).dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text("X", fontSize = ((BASE_FRETMARKER_SIZE * scale)).dp.value.sp)
    }
}

@Composable
private fun FretMarker(marker: FretboardMarker.FrettedNote?, scale: Float = 1.5f) {
    Box(
        modifier = Modifier
            .size(
                width = (BASE_FRET_WIDTH * scale).dp,
                height = (BASE_FRETMARKER_CONTAINER_HEIGHT * scale).dp
            ),
        contentAlignment = Alignment.Center
    ) {
        if (marker != null) {
            Box(
                modifier = Modifier
                    .size((BASE_FRETMARKER_SIZE * scale).dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ){
                marker.pitch?.let {
                    Text(
                        modifier = Modifier.fillMaxHeight(),
                        text = it.toString().replace("s", "#"),
                        color = Color.White,
                    )
                }
            }
        }
    }
}

// ... Data classes and extensions remain largely the same, included for completeness ...

sealed class FretboardMarker {
    data class FrettedNote(
        @IntRange(from = 1)
        val stringNumber: Int,
        @IntRange(from = 0)
        val fretNumber: Int,
        val tuning: (stringNumber: Int) -> PitchClass = standardTuningSixString()
    ) : FretboardMarker() {
        val pitch: PitchClass?
            get() = if (fretNumber >= 0) PitchClass.entries[((fretNumber + tuning(stringNumber).ordinal) % 12)] else null
    }

    data class MutedString(
        @IntRange(from = 1)
        val stringNumber: Int
    ) : FretboardMarker()
}

fun standardTuningSixString(): (stringNumber: Int) -> PitchClass = { stringNumber ->
    when (stringNumber) {
        1 -> PitchClass.E
        2 -> PitchClass.B
        3 -> PitchClass.G
        4 -> PitchClass.D
        5 -> PitchClass.A
        6 -> PitchClass.E
        else -> PitchClass.E // For any other string
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
                value.toIntOrNull() != null -> FretboardMarker.FrettedNote(index + 1, value.toInt())
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

private const val BASE_FRETMARKER_CONTAINER_HEIGHT = 16
private const val BASE_FRETBOARD_HEIGHT = 6 * BASE_FRETMARKER_CONTAINER_HEIGHT
private const val BASE_FRET_WIDTH = 24
private const val BASE_STRING_LEFT_PADDING = 10
private const val BASE_FRETMARKER_SIZE = 14
private const val BASE_MUTED_MARKER_RIGHT_PADDING = 4
private const val BASE_NUT_COLUMN_WIDTH = 16
private const val BASE_NUT_WIDTH = 8
private const val BASE_FRETWIRE_WIDTH = 3
private const val BASE_GUITAR_STRING_THICKNESS = 5
private const val BASE_FRET_BOARD_GUTTER_HEIGHT = 16
private const val BASE_FRET_NUMBER_GUTTER_PADDING_RIGHT = 4


enum class PitchClass {
    C, Cs, D, Ds, E, F, Fs, G, Gs, A, As, B;

    override fun toString(): String {
        return name.replace("s", "#")
    }
}