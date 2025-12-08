package com.chordbay.app.data.helper

import android.content.ContentResolver
import com.chordbay.app.data.model.chord.Chords
import com.chordbay.app.data.model.chord.HBFormat
import kotlin.math.roundToInt

fun pluralText(msg: String, count: Int): String {
    return if (count in 0..1) msg else msg + "s"
}

fun calculatePercentage(range: IntRange, value: Float): Int {
    val percentage = ((value) / (range.last - range.first)) * 100
    return percentage.roundToInt()
}

fun findKey(song: String, hbFormat: HBFormat, songHBFormat: HBFormat): String? {
    val openBracketIndices = song.indices.filter { song[it] == '[' }
    if (openBracketIndices.isEmpty()) {
        return null
    }
    val baseChord = Chords.getBaseChordsList(songHBFormat)
        .sortedByDescending { it.value.length }
    val firstChord = openBracketIndices.mapNotNull { index ->
        val textAfterOpenBracket = song.substring(startIndex = index)
        val closeBracketIndexInSubstring = textAfterOpenBracket.indexOf(']')
        if (closeBracketIndexInSubstring == -1) {
            null
        } else {
            textAfterOpenBracket.substring(1)
                .substringBefore(']')
        }
    }.mapNotNull { chordCandidate ->
        // Find the longest matching base chord
        val matchingBaseChord = baseChord.find { baseChord ->
            chordCandidate.startsWith(baseChord.value)
        } ?: return@mapNotNull null
        matchingBaseChord
    }.firstOrNull()?.value
    return firstChord
}

fun String.isPasswordValid(): Boolean =
    this.length >= 9 &&
            this.any { it.isUpperCase() } &&
            this.any { it.isDigit() } &&
            this.any { it.isLowerCase() }


fun ContentResolver.getFileName(uri: android.net.Uri): String? {
    return query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
    }
}