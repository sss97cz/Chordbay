package com.chordbay.app.data.mappers

import com.chordbay.app.data.database.song.SongEntity
import com.chordbay.app.data.model.Song
import com.chordbay.app.data.model.chord.HBFormat
import com.chordbay.app.data.remote.model.SongDto

fun SongEntity.toSong(): Song =
    Song(
        localId = id,
        remoteId = remoteId,
        title = title,
        artist = artist,
        content = content,
        hBFormat = hBFormat
    )
fun SongDto.toSong(): Song =
    Song(
        remoteId = id,
        title = title,
        artist = artist,
        content = content,
        isPublic = isPublic,
        hBFormat = if(germanNotation) HBFormat.GER else HBFormat.ENG
    )
fun Song.toDto(isRequest: Boolean = true): SongDto =
    SongDto(
        id = if (isRequest) null else remoteId,
        title = title,
        artist = artist,
        content = content,
        isPublic = isPublic,
        germanNotation = hBFormat == HBFormat.GER
    )
fun Song.toSongEntity(): SongEntity =
    SongEntity(
        id = localId ?: 0,
        remoteId = remoteId,
        title = title,
        artist = artist,
        content = content,
        hBFormat = hBFormat
    )