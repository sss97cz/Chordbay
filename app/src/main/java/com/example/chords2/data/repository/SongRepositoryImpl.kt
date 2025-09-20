package com.example.chords2.data.repository

import android.util.Log
import com.example.chords2.data.database.SongDao
import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.mappers.toSong
import com.example.chords2.data.mappers.toSongEntity
import com.example.chords2.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class SongRepositoryImpl(
    private val songDao: SongDao
) : SongRepository {

    override fun getAllSongs(): Flow<List<Song>> = songDao.getAllSongs().map { it.map { it.toSong() } }

    override fun getSongById(id: Int): Flow<Song?> {
        Log.d("SongRepositoryImpl", "getSongById called with id: $id")
        val temp = songDao.getSongById(id).map { it?.toSong() }
        return temp
    }

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
        songDao.insertSong(
            SongEntity(
                title = song.title,
                artist = song.artist,
                content = song.content
            )
        )
    }
}