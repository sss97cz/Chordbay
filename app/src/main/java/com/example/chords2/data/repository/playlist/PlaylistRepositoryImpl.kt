package com.example.chords2.data.repository.playlist

import com.example.chords2.data.database.playlist.PlaylistDao
import com.example.chords2.data.database.playlist.PlaylistEntity
import com.example.chords2.data.database.playlist.PlaylistSongCrossRef
import com.example.chords2.data.database.playlist.PlaylistWithSongs
import com.example.chords2.data.mappers.toSong
import com.example.chords2.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
): PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> =
        playlistDao.getAllPlaylists()


    override fun getSongsInPlaylist(playlistId: Int): Flow<List<Song>> =
        playlistDao.getPlaylistWithSongs(playlistId).map { playlistWithSongs: PlaylistWithSongs? ->
            playlistWithSongs?.songs?.map { it.toSong() } ?: emptyList()
        }

    override suspend fun makePlaylist(name: String) =
        playlistDao.insertPlaylist(PlaylistEntity(name = name))


    override suspend fun addSongToPlaylist(playlistId: Int, song: Song) {
        if (song.localId == null) return
        playlistDao.addSongToPlaylist(
            crossRef = PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = song.localId
            )
        )

    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }



}