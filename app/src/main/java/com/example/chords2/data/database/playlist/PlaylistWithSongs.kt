package com.example.chords2.data.database.playlist

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.chords2.data.database.song.SongEntity

data class PlaylistWithSongs(
    @Embedded
    val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PlaylistSongCrossRef::class,
            parentColumn = "playlistId",
            entityColumn = "song_id_in_cross_ref"
        )
    )
    val songs: List<SongEntity>
)
