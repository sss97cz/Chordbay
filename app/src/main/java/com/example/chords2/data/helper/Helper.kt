package com.example.chords2.data.helper

import android.content.ContentResolver
import com.example.chords2.data.model.util.Chords
import com.example.chords2.data.model.util.Chords.Companion.toCanonical
import com.example.chords2.data.model.util.Chords.Companion.transpose
import com.example.chords2.data.model.util.HBFormat
import kotlin.math.roundToInt

fun pluralText(msg: String, count: Int): String {
    return if (count in 0..1) msg else msg + "s"
}

fun calculatePercentage(range: IntRange, value: Float): Int {
    val percentage = ((value) / (range.last - range.first)) * 100
    return percentage.roundToInt()
}

fun findKey(song: String, hbFormat: HBFormat, songHBFormat: HBFormat): String? {
    val openBracketIndex = song.indexOf('[')
    if (openBracketIndex == -1) {
        return null
    }
    val textAfterOpenBracket = song.substring(startIndex = openBracketIndex)
    val closeBracketIndexInSubstring = textAfterOpenBracket.indexOf(']')
    if (closeBracketIndexInSubstring == -1) {
        return null
    }
    val firstChord = textAfterOpenBracket.substring(1)
        .substringBefore(']')
    if (firstChord.isEmpty()) {
        return null
    }
    val formatsMatch = hbFormat == songHBFormat
    val baseChord = Chords.getBaseChordsList(songHBFormat)
        .sortedByDescending { it.value.length }
        .firstOrNull { firstChord.startsWith(it.value) }

    return baseChord?.value
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