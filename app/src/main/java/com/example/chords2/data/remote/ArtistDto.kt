package com.example.chords2.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDto(
    val name: String,
    val songCount: Int
)
