package com.example.chords2.data.helper

import com.example.chords2.data.model.util.Chords
import kotlin.math.roundToInt

fun pluralText(msg: String, count: Int): String {
    return if (count in 0..1) msg else msg + "s"
}

fun calculatePercentage(range: IntRange, value: Float): Int {
    val percentage = ((value) / (range.last - range.first)) * 100
    return percentage.roundToInt()
}

fun findKey(song: String): String? {
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
    // kotlin
    val baseChord = Chords.allBaseChords
        .sortedByDescending { it.value.length }
        .firstOrNull { firstChord.startsWith(it.value) }

    return baseChord?.value
}