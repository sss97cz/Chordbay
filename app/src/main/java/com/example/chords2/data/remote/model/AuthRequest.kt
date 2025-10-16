package com.example.chords2.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthRequest(
    @Json(name = "email")
    val email: String,
    @Json(name = "password")
    val password: String
)