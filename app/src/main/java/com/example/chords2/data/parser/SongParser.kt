package com.example.chords2.data.parser

import android.util.Log

object SongParser {
    val TAG = "SongParser"

    fun convertToFormated(raw: String): String {
        val positionOfBrackets = raw.findBrackets()
        val numberOfLines = raw.count { it == '\n' }
        if (positionOfBrackets == null) {
            return raw
        }
        val formated = raw.moveBracketsUp(positionOfBrackets)

        Log.d(TAG, "Number of lines: $numberOfLines")
        return raw
    }

    fun convertToRaw(formated: String): String {
        return formated
    }

    fun String.findBrackets(): List<Pair<Int, Int>>? {
        if (!this.contains('[')) {
            return null
        }
        val bracketsList: MutableList<Pair<Int, Int>?> = mutableListOf()
        this.mapIndexedTo(bracketsList) { index, c ->
            if (c == '[') {
                Pair(
                    first = index,
                    second = this.indexOf(']', index)
                )
            } else {
                null
            }
        }
        return bracketsList.filterNotNull()
    }

    private fun String.moveBracketsUp(positionOfBrackets: List<Pair<Int, Int>>): String {
        Log.d(TAG, "Position of brackets: $positionOfBrackets")
        val bracketsContent: List<ChordBracket> = positionOfBrackets.map { it ->
            val content = this.substring(it.first + 1, it.second)
            val offset: Int = calculateOffset(
                it,
                this
            )
            Log.d(TAG, "Offset: $offset")
            ChordBracket(
                startIndex = it.first,
                endIndex = it.second,
                content = content,
                offset = offset,
                onLine = this.substring(0, it.first).count { char -> char == '\n' }
            )
        }
        Log.d(TAG, "Brackets content: $bracketsContent")
        var withoutBrackets: String = this
        positionOfBrackets.reversed().forEach {
            withoutBrackets = withoutBrackets.replaceRange(
                it.first,
                it.second + 1,
                ""
            )
        }
        val textLines = withoutBrackets.split("\n")
        val chordsGroupedByLine: Map<Int, List<String>> = bracketsContent
            .groupBy { it.onLine }
            .mapValues { entry ->
                entry.value.map {
                    var offset = ""
                    repeat(it.offset) {
                        offset += " "
                    }
                    offset + it.content
                }
            }
        chordsGroupedByLine.toSortedMap().forEach { (lineNumber, chordContents) ->
            Log.d(TAG, "Line $lineNumber Chords: ${chordContents.joinToString(" ")}")
        }
        Log.d(TAG, "Without brackets: $withoutBrackets")
        val sb = StringBuilder()
        textLines.forEachIndexed { index, line ->
            if (chordsGroupedByLine.containsKey(index)) {
                sb.append(chordsGroupedByLine[index]?.joinToString(""))
                    .append("\n")
                    .append(line)
                    .append("\n")
            } else {
                sb.append(line).append("\n")
            }
        }


        Log.d(TAG, "SB: ${sb.toString()}")
        return sb.toString()
    }


    private fun calculateOffset(bracketPair: Pair<Int, Int>, raw: String): Int {
        val first = bracketPair.first
        Log.d(TAG, "First: $first")
        if (first == 0) {
            return 0
        }
        if (raw[first - 1] == '\n') {
            return 0
        }
        val substring = raw.substring(0, first).reversed()
        substring.forEachIndexed { index, char ->
            Log.d(TAG, "Char: ${char.code}")
//            if (char == raw[0]) {
//                return index + 1
//            }
            when (char) {
                '[' -> return first - (first - index)
                ']' -> return first - (first - index)
                '\n' -> return first - (first - index)
            }
        }
        return 0
    }


    data class ChordBracket(
        val startIndex: Int,
        val endIndex: Int,
        val content: String,
        val offset: Int,
        val onLine: Int,
        val isChord: Boolean = true,
    )
}

