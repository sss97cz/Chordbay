package com.chordbay.app.data.helper

import com.chordbay.app.data.model.Song
import com.chordbay.app.data.model.chord.HBFormat
import kotlin.text.Regex

object TxtSongIO {

    // EXPORT: return just the raw content (no header)
    fun songToTxtContent(song: Song): String {
        return normalizeLineEndings(song.content)
    }

    // Build the desired file name: "Artist - Title.txt"
    fun buildFileName(song: Song): String {
        val artistPart = sanitizeForFileName(song.artist.ifBlank { "Unknown Artist" })
        val titlePart = sanitizeForFileName(song.title.ifBlank { "Untitled" })
        return "$artistPart - $titlePart.txt"
    }

    // IMPORT: given raw content + file name + user-selected HBFormat
    fun txtToSong(
        rawContent: String,
        fileName: String,
        userHBFormat: HBFormat
    ): Song {
        val (artist, title) = parseArtistTitleFromFileName(fileName)
        val normalizedContent = normalizeLineEndings(rawContent)
        return Song(
            title = title,
            artist = artist,
            content = normalizedContent,
            hBFormat = userHBFormat // Always use user's setting
        )
    }

    fun parseArtistTitleFromFileName(fileName: String): Pair<String, String> {
        // Strip extension
        val base = fileName.removeSuffix(".txt")
        val idx = base.indexOf(" - ")
        return if (idx >= 0) {
            val artist = base.substring(0, idx).trim().ifBlank { "Unknown Artist" }
            val title = base.substring(idx + 3).trim().ifBlank { "Untitled" }
            artist to title
        } else {
            // Fallback: whole name becomes title
            "Unknown Artist" to base.trim().ifBlank { "Untitled" }
        }
    }

    private fun sanitizeForFileName(s: String): String =
        s.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()

    private fun normalizeLineEndings(s: String) =
        s.replace("\r\n", "\n").replace("\r", "\n")
}