package com.chordbay.app.data.model

import com.chordbay.app.data.model.chord.HBFormat

data class Song(
    val localId: Int? = null,
    val remoteId: String? = null,
    val title: String,
    val artist: String,
    val content: String,
    val isPublic: Boolean = true,
    val markSynced: Boolean = false,
    val hBFormat: HBFormat
)
