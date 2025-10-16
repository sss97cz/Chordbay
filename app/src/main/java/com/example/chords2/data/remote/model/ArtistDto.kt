package com.example.chords2.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDto(
    val name: String,
    val songCount: Int
)