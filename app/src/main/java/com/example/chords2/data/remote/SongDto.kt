package com.example.chords2.data.remote

import com.example.chords2.data.model.SongUi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongDto(
    @Json(name = "id")
    val id: String? = null,
    @Json(name = "title")
    val title: String,
    @Json(name = "artist")
    val artist: String,
    @Json(name = "content")
    val content: String
)