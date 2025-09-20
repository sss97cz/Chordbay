package com.example.chords2.data.mappers

import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.SongUi
import com.example.chords2.data.remote.SongDto

fun Song.toSongUi(): SongUi =
    SongUi(
        title = title,
        artist = artist,
        content = content,
    )
fun SongEntity.toSong(): Song =
    Song(
        id = id.toString(),
        title = title,
        artist = artist,
        content = content,
    )
fun SongDto.toSong(): Song =
    Song(
        id = id,
        title = title,
        artist = artist,
        content = content,
    )
fun Song.toDto(): SongDto =
    SongDto(
        id = id,
        title = title,
        artist = artist,
        content = content,
    )
fun Song.toSongEntity(): SongEntity =
    SongEntity(
        id = id.toInt(),
        title = title,
        artist = artist,
        content = content,
    )

fun SongUi.toSong(): Song =
    Song(
        id = "",
        title = title,
        artist = artist,
        content = content,
    )