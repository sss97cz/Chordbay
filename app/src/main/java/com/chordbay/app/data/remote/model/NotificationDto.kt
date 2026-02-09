package com.chordbay.app.data.remote.model

import com.squareup.moshi.JsonClass
import java.time.Instant

@JsonClass(generateAdapter = true)
data class NotificationDto(
    val id: String,
    val title: String,
    val message: String,
    val minVersion: Int,
    val maxVersion: Int,
    val activeInSeconds: Long,
    val expiredInSeconds: Long?
)
