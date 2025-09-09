package com.example.chords2.data.repository

import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<SongEntity>>

    fun getSongById(id: Int): Flow<SongEntity?>

    suspend fun updateSong(song: SongEntity)

    suspend fun insertSong(song: SongEntity): Long

    suspend fun deleteSong(song: SongEntity)
}