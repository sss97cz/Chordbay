package com.chordbay.app.data.repository.playlist

import com.chordbay.app.data.database.playlist.PlaylistEntity
import com.chordbay.app.data.model.Song
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    fun getSongsInPlaylist(playlistId: Int): Flow<List<Song>>
    suspend fun makePlaylist(name: String)

    suspend fun addSongToPlaylist(playlistId: Int, song: Song)

    suspend fun deletePlaylist(playlist: PlaylistEntity)
}