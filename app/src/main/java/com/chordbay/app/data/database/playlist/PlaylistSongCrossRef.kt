package com.chordbay.app.data.database.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.chordbay.app.data.database.song.SongEntity

@Entity(
    tableName = "playlists_song_cross_ref",
    primaryKeys = ["playlistId", "song_id_in_cross_ref"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["song_id_in_cross_ref"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("playlistId"),
        Index("song_id_in_cross_ref")
    ]
)
data class PlaylistSongCrossRef(
    val playlistId: Int,
    @ColumnInfo(name = "song_id_in_cross_ref")
    val songId: Int,
    val position: Int? = null,
)
