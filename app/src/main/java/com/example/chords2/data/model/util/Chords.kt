package com.example.chords2.data.model.util

import android.util.Log
import com.example.chords2.data.model.util.Chords.A.add5
import com.example.chords2.data.model.util.Chords.A.add7
import com.example.chords2.data.model.util.Chords.A.addMoll
import com.example.chords2.data.model.util.Chords.A.addMoll7

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
    companion object{
        val allBaseChords = listOf(
            C, CSharp, D, DSharp, E, F, Fsharp, G, GSharp, A, B, H
        )

        fun allChordsToString(): List<String> {
            val base = allBaseChords.map { it.value }

            val minors = allBaseChords.flatMap {
                listOf(
                    addMoll(it, MollFormat.M),
                    addMoll(it, MollFormat.Mi)
                )
            }
            val sevenths = allBaseChords.map { add7(it) }
            val fifths = allBaseChords.map { add5(it) }
            val minorSevenths = allBaseChords.map { addMoll7(it) }
            return base + minors + sevenths + fifths + minorSevenths
        }
        // Extension for base chord
        fun Chords.transpose(semitones: Int): Chords {
            val index = Chords.allBaseChords.indexOf(this)
            require(index >= 0) { "Chord not in allBaseChords: ${this.value}" }
            val newIndex = (index + semitones).mod(allBaseChords.size)
            return Chords.allBaseChords[newIndex]
        }

        // Extension for chord strings, e.g. "C#m7"
        fun String.transposeChord(semitones: Int): String {
            if(semitones.mod(12) == 0){
                Log.d("TransposeChord", "Transposing $this by $semitones semitones and returning")
                return this
            }
            Log.d("TransposeChord", "Transposing $this by $semitones semitones")
            val baseChord = allBaseChords.firstOrNull { this.startsWith(it.value) }
                ?: return this // if it doesn't match, return as-is
            Log.d("TransposeChord", "Transposing $this by $semitones semitones")
            val suffix = this.removePrefix(baseChord.value)
            return baseChord.transpose(semitones).value + suffix
        }
    }

    fun addMoll(
        chord: Chords,
        mollFormat: MollFormat = MollFormat.Mi
    ): String{
        return chord.value + mollFormat.value
    }
    fun add7(chord: Chords) = chord.value + "7"

    fun addMoll7(chord: Chords) = addMoll(chord) + "7"
    fun add5(chord: Chords) = chord.value + "5"

}

sealed class MollFormat(val value: String) {
    object Mi : MollFormat("mi")
    object M : MollFormat("m")
}