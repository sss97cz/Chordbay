package com.chordbay.app.data.helper

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import com.chordbay.app.data.model.chord.Chords
import com.chordbay.app.data.model.chord.HBFormat
import kotlin.math.roundToInt
import androidx.core.net.toUri
import java.net.URLEncoder

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


fun openExternalApp(context: android.content.Context, appName: String, query: String) {
    val encoded = java.net.URLEncoder.encode(query, "UTF-8")

    when (appName) {
        "Spotify" -> {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "spotify:search:$encoded".toUri()
                setPackage("com.spotify.music")
            }
            tryLaunch(context, intent, "https://open.spotify.com/search/$encoded")
        }
        "YouTube" -> {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://www.youtube.com/results?search_query=$encoded".toUri()
                setPackage("com.google.android.youtube")
            }
            tryLaunch(context, intent, "https://www.youtube.com/results?search_query=$encoded")
        }
        "YouTube Music" -> {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://music.youtube.com/search?q=$encoded".toUri()
                setPackage("com.google.android.apps.youtube.music")
            }
            tryLaunch(context, intent, "https://music.youtube.com/search?q=$encoded")
        }
        else -> {
            // "Browser" or default search
            // This intent asks the system to perform a web search for the query string.
            // It respects the user's default browser and search engine settings.
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, query)
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Fallback if no web search provider is found (rare, but good practice)
                val fallbackIntent = Intent(Intent.ACTION_VIEW,
                    "https://www.google.com/search?q=$encoded".toUri())
                context.startActivity(fallbackIntent)
            }
        }
    }
}

// Helper to handle the "try app, fallback to browser" logic
fun tryLaunch(context: android.content.Context, primaryIntent: Intent, fallbackUrl: String) {
    try {
        context.startActivity(primaryIntent)
    } catch (e: Exception) {
        val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUrl.toUri())
        try {
            context.startActivity(fallbackIntent)
        } catch (e2: Exception) {
            // Handle extremely rare case where no browser is installed
        }
    }
}