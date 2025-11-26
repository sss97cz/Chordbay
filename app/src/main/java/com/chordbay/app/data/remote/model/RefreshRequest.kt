package com.chordbay.app.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefreshRequest(
    @Json(name = "refreshToken")
    val refreshToken: String
)