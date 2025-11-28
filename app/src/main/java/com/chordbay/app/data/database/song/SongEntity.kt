package com.chordbay.app.data.database.song

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chordbay.app.data.model.chord.HBFormat

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteId: String? = null,
    val title: String = "",
    val artist: String = "",
    val content: String = "",
    val hBFormat: HBFormat,
)