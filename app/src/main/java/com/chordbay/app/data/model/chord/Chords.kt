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
            val minorSevenths =  baseChords.map { addMoll7(it, MollFormat.Mi) } + baseChords.map { addMoll7(it, MollFormat.M) }
            return baseChords.map{it.value} + minors + sevenths + fifths + minorSevenths
        }

        // Extension for base chord
        fun Chords.toCanonical(): Chords = when (this) {
            Bb -> B
            BEng -> H
            else -> this
        }

        fun Chords.transpose(semitones: Int, hbFormat: HBFormat): Chords {
            // compute index against the canonical allBaseChords list
            val canonicalIndex = allBaseChords.indexOf(this.toCanonical())
            require(canonicalIndex >= 0) { "Chord not in allBaseChords: ${this.value}" }
            val newIndex = (canonicalIndex + semitones).mod(12)
            // return the chord from the requested format's list at the same positional index
            val targetList = getBaseChordsList(hbFormat)
            return targetList[newIndex]
        }

        // Extension for chord strings, e.g. "C#m7"
        fun String.transposeChord(semitones: Int, hbFormat: HBFormat, songFormat: HBFormat): String {
            val baseChord = getBaseChordsList(songFormat)
                .sortedByDescending { it.value.length }
                .firstOrNull { this.startsWith(it.value) }
                ?: return this // if it doesn't match, return as-is

            val suffix = this.removePrefix(baseChord.value)
            return baseChord.transpose(semitones, hbFormat).mapBaseChord(hbFormat, songFormat) + suffix
        }
        fun Chords.toOrdinal(hbFormat: HBFormat): Int {
            val targetList = getBaseChordsList(hbFormat)
            return targetList.indexOfFirst { it.toCanonical() == this.toCanonical() }
        }
        fun String.toCanonicalChordName(songFormat: HBFormat): String {
            val baseChord = getBaseChordsList(songFormat)
                .sortedByDescending { it.value.length }
                .firstOrNull { this.startsWith(it.value) }
                ?: return this

            val suffix = this.removePrefix(baseChord.value)
            val canonicalBase = baseChord.toCanonical().value
            return canonicalBase + suffix
        }
    }

    fun addMoll(
        chord: Chords,
        mollFormat: MollFormat = MollFormat.Mi
    ): String {
        return chord.value + mollFormat.value
    }

    fun add7(chord: Chords) = chord.value + "7"

    fun addMoll7(chord: Chords, mollFormat: MollFormat) = addMoll(chord, mollFormat) + "7"
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


sealed class ChordQuality {
    object Major : ChordQuality()
    object Minor : ChordQuality()
    object Minor7 : ChordQuality()
    object Seven : ChordQuality()
    object Five : ChordQuality()
}
object ChordParser {
    fun parseCanonicalChord(canonical: String): Pair<Chords, ChordQuality>? {
        val base = Chords.allBaseChords
            .sortedByDescending { it.value.length }
            .firstOrNull { canonical.startsWith(it.value) }
            ?: return null

        val suffix = canonical.removePrefix(base.value)
        val quality = when (suffix) {
            "" -> ChordQuality.Major
            "mi" -> ChordQuality.Minor
            "m" -> ChordQuality.Minor
            "mi7" -> ChordQuality.Minor7
            "m7" -> ChordQuality.Minor7
            "7" -> ChordQuality.Seven
            "5" -> ChordQuality.Five
            else -> return null
        }
        return base to quality
    }
}
object ChordFingerings {

    fun getFingeringForChord(
        chordText: String,
        songFormat: HBFormat
    ): String? = with(Chords) {
        val canonical = chordText.toCanonicalChordName(songFormat)
        val parsed = ChordParser.parseCanonicalChord(canonical) ?: return null
        val (base, quality) = parsed
        fingeringFor(base, quality)
    }

    private fun fingeringFor(
        base: Chords,
        quality: ChordQuality
    ): String = when (base) {

        // ---------------------- E ----------------------
        is Chords.E -> when (quality) {
            ChordQuality.Major  -> "0|0|1|2|2|0"
            ChordQuality.Minor  -> "0|0|0|2|2|0"
            ChordQuality.Minor7 -> "0|3|0|2|2|0"
            ChordQuality.Seven  -> "0|2|0|1|0|0"
            ChordQuality.Five   -> "x|x|x|2|2|0"
        }

        // ---------------------- C ----------------------
        is Chords.C -> when (quality) {
            ChordQuality.Major  -> "0|1|0|2|3|x"      // C
            ChordQuality.Minor  -> "3|4|5|5|3|x"      // Cm (barre A-shape)
            ChordQuality.Minor7 -> "3|4|3|5|3|x"      // Cm7
            ChordQuality.Seven  -> "0|1|3|2|3|x"      // C7 open
            ChordQuality.Five   -> "x|x|5|5|3|x"      // C5
        }

        // ---------------------- C# / Db ----------------------
        is Chords.CSharp -> when (quality) {
            ChordQuality.Major  -> "4|6|6|6|4|x"      // C# (barre)
            ChordQuality.Minor  -> "4|5|6|6|4|x"      // C#m
            ChordQuality.Minor7 -> "4|5|4|6|4|x"      // C#m7
            ChordQuality.Seven  -> "x|2|4|3|4|x"      // C#7 (E-shape)
            ChordQuality.Five   -> "x|x|6|6|4|x"      // C#5
        }

        // ---------------------- D ----------------------
        is Chords.D -> when (quality) {
            ChordQuality.Major  -> "2|3|2|0|x|x"
            ChordQuality.Minor  -> "1|3|2|0|x|x"
            ChordQuality.Minor7 -> "1|1|2|0|x|x"
            ChordQuality.Seven  -> "2|1|2|0|x|x"
            ChordQuality.Five   -> "x|x|7|7|5|x"
        }

        // ---------------------- D# / Eb ----------------------
        is Chords.DSharp -> when (quality) {
            ChordQuality.Major  -> "6|8|8|8|6|x"
            ChordQuality.Minor  -> "6|7|8|8|6|x"
            ChordQuality.Minor7 -> "6|7|4|8|6|x"
            ChordQuality.Seven  -> "6|8|4|8|6|x"
            ChordQuality.Five   -> "x|x|8|8|6|x"
        }

        // ---------------------- F ----------------------
        is Chords.F -> when (quality) {
            ChordQuality.Major  -> "1|1|2|3|3|1"      // Full barre
            ChordQuality.Minor  -> "1|1|1|3|3|1"      // Fm
            ChordQuality.Minor7 -> "1|1|1|1|3|1"      // Fm7
            ChordQuality.Seven  -> "1|1|2|1|3|1"      // F7
            ChordQuality.Five   -> "x|x|x|3|3|1"      // F5
        }

        // ---------------------- F# / Gb ----------------------
        is Chords.Fsharp -> when (quality) {
            ChordQuality.Major  -> "2|2|3|4|4|2"
            ChordQuality.Minor  -> "2|2|2|4|4|2"
            ChordQuality.Minor7 -> "2|2|2|2|4|2"
            ChordQuality.Seven  -> "2|2|3|2|4|2"
            ChordQuality.Five   -> "x|x|x|4|4|2"
        }

        // ---------------------- G ----------------------
        is Chords.G -> when (quality) {
            ChordQuality.Major  -> "3|3|0|0|2|3"
            ChordQuality.Minor  -> "3|3|3|5|5|3"      // Gm (barre)
            ChordQuality.Minor7 -> "3|3|3|3|5|3"      // Gm7
            ChordQuality.Seven  -> "1|0|0|0|2|3"      // G7
            ChordQuality.Five   -> "x|x|x|5|5|3"
        }

        // ---------------------- G# / Ab ----------------------
        is Chords.GSharp -> when (quality) {
            ChordQuality.Major  -> "4|4|5|6|6|4"
            ChordQuality.Minor  -> "4|4|4|6|6|4"
            ChordQuality.Minor7 -> "4|4|4|4|6|4"
            ChordQuality.Seven  -> "4|4|5|4|6|4"
            ChordQuality.Five   -> "x|x|x|6|6|4"
        }

        // ---------------------- A ----------------------
        is Chords.A -> when (quality) {
            ChordQuality.Major  -> "0|2|2|2|0|x"
            ChordQuality.Minor  -> "0|1|2|2|0|x"
            ChordQuality.Minor7 -> "0|1|0|2|0|x"
            ChordQuality.Seven  -> "0|2|0|2|0|x"
            ChordQuality.Five   -> "x|x|2|2|0|x"
        }

        // ---------------------- B ----------------------
        is Chords.B -> when (quality) {
            ChordQuality.Major  -> "1|3|3|3|1|x"
            ChordQuality.Minor  -> "1|2|3|3|1|x"
            ChordQuality.Minor7 -> "1|2|1|3|1|x"
            ChordQuality.Seven  -> "1|3|1|3|1|x"
            ChordQuality.Five   -> "x|x|3|3|1|x"
        }

        // ---------------------- H (a.k.a. B in German) ----------------------
        is Chords.H -> when (quality) {
            ChordQuality.Major  -> "2|4|4|4|2|x"      // same as B
            ChordQuality.Minor  -> "2|3|4|4|2|x"
            ChordQuality.Minor7 -> "2|3|2|4|2|x"
            ChordQuality.Seven  -> "3|0|3|2|3|x"
            ChordQuality.Five   -> "x|x|4|4|2|x"
        }

        is Chords.Bb -> error("Use canonical form B")
        is Chords.BEng -> error("Use canonical form H")
    }
}