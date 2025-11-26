package com.chordbay.app.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ArtistDto(
    val name: String,
    val songCount: Int
)