package com.example.chords2.data.mappers

import com.example.chords2.data.database.song.SongEntity
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.SongUi
import com.example.chords2.data.remote.model.SongDto

fun Song.toSongUi(): SongUi =
    SongUi(
        title = title,
        artist = artist,
        content = content,
    )
fun SongEntity.toSong(): Song =
    Song(
        localId = id,
        remoteId = remoteId,
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
fun Song.toDto(isRequest: Boolean = true): SongDto =
    SongDto(
        id = if (isRequest) null else remoteId,
        title = title,
        artist = artist,
        content = content,
    )
fun Song.toSongEntity(): SongEntity =
    SongEntity(
        id = localId ?: 0,
        remoteId = remoteId,
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