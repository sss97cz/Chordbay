package com.example.chords2.data.model

import com.example.chords2.data.model.util.HBFormat

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
