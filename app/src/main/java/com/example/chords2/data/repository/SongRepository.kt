package com.example.chords2.data.repository

import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getAllSongs(): Flow<List<Song>>

    fun getSongById(id: Int): Flow<Song?>

    suspend fun updateSong(song: Song)

    suspend fun insertSong(song: Song): Long
    suspend fun insertSong(): Long

    suspend fun deleteSong(song: Song)

    suspend fun insertRemoteSong(song: Song)
}