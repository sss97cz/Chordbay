package com.example.chords2.data.repository.song

import com.example.chords2.data.database.song.SongDao
import com.example.chords2.data.database.song.SongEntity
import com.example.chords2.data.mappers.toSong
import com.example.chords2.data.mappers.toSongEntity
import com.example.chords2.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongRepositoryImpl(
    private val songDao: SongDao
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs().map { it.map { it.toSong() } }

    override fun getSongById(id: Int): Flow<Song?> =
        songDao.getSongById(id).map { it?.toSong() }

    override suspend fun insertSong(song: Song) =
        songDao.insertSong(song.toSongEntity())

    override suspend fun insertSong(): Long =
        songDao.insertSong(SongEntity())

    override suspend fun updateSong(song: Song) {
        songDao.updateSong(song.toSongEntity())
    }

    override suspend fun deleteSong(song: Song) {
        songDao.deleteSong(song.toSongEntity())
    }

    override suspend fun insertRemoteSong(song: Song) {
        songDao.insertSong(song.toSongEntity())
    }
}