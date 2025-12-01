package com.chordbay.app.data.repository.playlist

import com.chordbay.app.data.database.playlist.PlaylistDao
import com.chordbay.app.data.database.playlist.PlaylistEntity
import com.chordbay.app.data.database.playlist.PlaylistSongCrossRef
import com.chordbay.app.data.database.playlist.PlaylistWithSongs
import com.chordbay.app.data.mappers.toSong
import com.chordbay.app.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
) : PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> =
        playlistDao.getAllPlaylists()

    override fun getSongsInPlaylist(playlistId: Int): Flow<List<Song>> =
        playlistDao.getSongsInPlaylistOrdered(playlistId)
            .map { entities -> entities.map { it.toSong() } }

    override suspend fun savePlaylistOrder(
        playlistId: Int,
        orderedSongIds: List<Int>
    ) {
        val crossRefs = playlistDao.getCrossRefsForPlaylist(playlistId)

        // Map: songId -> current crossRef
        val bySongId = crossRefs.associateBy { it.songId }

        val updated = orderedSongIds.mapIndexedNotNull { index, songId ->
            val ref = bySongId[songId] ?: return@mapIndexedNotNull null
            if (ref.position == index) {
                ref // unchanged, but Update can handle that
            } else {
                ref.copy(position = index)
            }
        }

        if (updated.isNotEmpty()) {
            playlistDao.updateCrossRefs(updated)
        }
    }

    override suspend fun makePlaylist(name: String) =
        playlistDao.insertPlaylist(PlaylistEntity(name = name))


    override suspend fun addSongToPlaylist(playlistId: Int, song: Song) {
        if (song.localId == null) return
        val playlist = playlistDao.getPlaylistWithSongs(playlistId).first()
        val position = playlist?.songs?.size
        playlistDao.addSongToPlaylist(
            crossRef = PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = song.localId,
                position = position
            )
        )
    }

    override suspend fun removeSongFromPlaylistAndReorder(playlistId: Int, songId: Int) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)

        val remaining = playlistDao.getCrossRefsForPlaylistOrdered(playlistId)

        remaining.forEachIndexed { index, crossRef ->
            if (crossRef.position != index) {
                playlistDao.updateCrossRef(
                    crossRef.copy(position = index)
                )
            }
        }
    }

    override suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) {
        playlistDao.updatePlaylist(playlist)
    }

    override suspend fun reorderPlaylistSongs(playlistId: Int, orderedLocalIds: List<Int>) {
        withContext(Dispatchers.IO) {
            playlistDao.reorderPlaylistSongs(playlistId, orderedLocalIds)
        }
    }
}