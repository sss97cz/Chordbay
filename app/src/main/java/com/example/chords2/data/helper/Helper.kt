package com.example.chords2.data.helper

import androidx.annotation.FloatRange
import com.example.chords2.data.model.util.Chords
import okhttp3.internal.checkOffsetAndCount
import java.awt.font.NumericShaper
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
    val baseChord = Chords.allBaseChords.firstOrNull {
        firstChord.contains(it.value)
    }
    return baseChord?.value
}