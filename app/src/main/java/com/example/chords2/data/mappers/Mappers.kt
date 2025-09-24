package com.example.chords2.data.mappers

import com.example.chords2.data.database.song.SongEntity
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
        localId = id,
        title = title,
        artist = artist,
        content = content,
    )
fun SongDto.toSong(): Song =
    Song(
        remoteId = id,
        title = title,
        artist = artist,
        content = content,
    )
fun Song.toDto(): SongDto =
    SongDto(
        title = title,
        artist = artist,
        content = content,
    )
fun Song.toSongEntity(): SongEntity =
    SongEntity(
        id = localId ?: 0,
        title = title,
        artist = artist,
        content = content,
    )

fun SongUi.toSong(): Song =
    Song(
        title = title,
        artist = artist,
        content = content,
    )