package com.chordbay.app.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthRequest(
    @property:Json(name = "email")
    val email: String,
    @property:Json(name = "password")
    val password: String
)