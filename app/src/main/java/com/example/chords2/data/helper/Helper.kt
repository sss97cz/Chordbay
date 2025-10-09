package com.example.chords2.data.helper

import androidx.annotation.FloatRange
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