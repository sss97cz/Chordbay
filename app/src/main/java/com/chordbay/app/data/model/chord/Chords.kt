package com.chordbay.app.data.model.chord

import android.util.Log
import com.chordbay.app.data.model.chord.Chords.A.add5
import com.chordbay.app.data.model.chord.Chords.A.add7
import com.chordbay.app.data.model.chord.Chords.A.addMoll
import com.chordbay.app.data.model.chord.Chords.A.addMoll7
import com.chordbay.app.data.model.chord.Chords.A.mapBaseChord

sealed class Chords(
    val value: String,
) {
    object C : Chords("C")
    object CSharp : Chords("C#")
    object D : Chords("D")
    object DSharp : Chords("D#")
    object E : Chords("E")
    object F : Chords("F")
    object Fsharp : Chords("F#")
    object G : Chords("G")
    object GSharp : Chords("G#")
    object A : Chords("A")
    object B : Chords("B")
    object H : Chords("H")

    object Bb : Chords("Bb") // For English notation
    object BEng : Chords("B") // For English notation
    companion object {
        val allBaseChords = listOf(
            C, CSharp, D, DSharp, E, F, Fsharp, G, GSharp, A, B, H
        )
        fun getBaseChordsList(hbFormat: HBFormat): List<Chords> {
            return if (hbFormat == HBFormat.ENG) {
                allBaseChords.map {
                    when (it) {
                        B -> Bb
                        H -> BEng
                        else -> it
                    }
                }
            } else allBaseChords
        }

        fun allChordsToString(hbFormat: HBFormat): List<String> {
            val baseChords = getBaseChordsList(hbFormat)

            val minors = baseChords.flatMap {
                listOf(
                    addMoll(it, MollFormat.M),
                    addMoll(it, MollFormat.Mi)
                )
            }
            val sevenths = baseChords.map { add7(it) }
            val fifths = baseChords.map { add5(it) }
            val minorSevenths = baseChords.map { addMoll7(it) }
            return baseChords.map{it.value} + minors + sevenths + fifths + minorSevenths.also {
                Log.d("AllChordsToString", "All chords: $it")
            }
        }

        // Extension for base chord
        fun Chords.toCanonical(): Chords = when (this) {
            Bb -> B
            BEng -> H
            else -> this
        }

        fun Chords.transpose(semitones: Int, hbFormat: HBFormat): Chords {
            Log.d("TransposeChord", "hbFormat: $hbFormat")
            // compute index against the canonical allBaseChords list
            val canonicalIndex = allBaseChords.indexOf(this.toCanonical())
            Log.d("TransposeChord", "canonical baseChords: ${allBaseChords.map { it.value }}")
            require(canonicalIndex >= 0) { "Chord not in allBaseChords: ${this.value}" }
            val newIndex = (canonicalIndex + semitones).mod(12)
            // return the chord from the requested format's list at the same positional index
            val targetList = getBaseChordsList(hbFormat)
            Log.d("TransposeChord", "target baseChords: ${targetList.map { it.value }} newIndex: $newIndex")
            return targetList[newIndex]
        }

        // Extension for chord strings, e.g. "C#m7"
        fun String.transposeChord(semitones: Int, hbFormat: HBFormat, songFormat: HBFormat): String {
            Log.d("TransposeChord", "Transposing $this by $semitones semitones")
            val baseChord = getBaseChordsList(songFormat)
                .sortedByDescending { it.value.length }
                .firstOrNull { this.startsWith(it.value) }
                ?: return this // if it doesn't match, return as-is

            val suffix = this.removePrefix(baseChord.value)
            return baseChord.transpose(semitones, hbFormat).mapBaseChord(hbFormat, songFormat) + suffix
        }

    }

    fun addMoll(
        chord: Chords,
        mollFormat: MollFormat = MollFormat.Mi
    ): String {
        return chord.value + mollFormat.value
    }

    fun add7(chord: Chords) = chord.value + "7"

    fun addMoll7(chord: Chords) = addMoll(chord) + "7"
    fun add5(chord: Chords) = chord.value + "5"
    fun Chords.mapBaseChord(userFormat: HBFormat?, songFormat: HBFormat): String? {
        if (userFormat == null) return this.value
        return when (userFormat) {
            HBFormat.GER -> if (songFormat == userFormat) this.value else when (this) {
                B -> "B"
                H -> "H"
                else -> this.value
            }

            HBFormat.ENG -> if (songFormat == userFormat) this.value else when (this) {
                B -> "Bb"
                H -> "B"
                else -> this.value
            }
        }
    }

}

sealed class MollFormat(val value: String) {
    object Mi : MollFormat("mi")
    object M : MollFormat("m")
}

enum class HBFormat(val value: String) {
    ENG("ENG (Bb/B)"),
    GER("GER (H/B)"),
}