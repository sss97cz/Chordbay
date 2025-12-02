package com.chordbay.app.data.model

import com.chordbay.app.data.database.playlist.PlaylistEntity

data class PlaylistInfo(
    val playlist: PlaylistEntity,
    val songCount: Int
)
