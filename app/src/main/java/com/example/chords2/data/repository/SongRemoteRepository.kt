package com.example.chords2.data.repository

import com.example.chords2.data.model.Song
import com.example.chords2.data.model.SongUi
import com.example.chords2.data.remote.ArtistDto
import com.example.chords2.data.remote.SongDto


interface SongRemoteRepository {
    suspend fun getSongs(): Result<List<Song>>
    suspend fun getSongById(id: String): Result<Song>
    suspend fun createSong(song: Song): Result<String>
    suspend fun updateSong(song: Song): Result<Boolean>
    suspend fun getAllArtists(): Result<List<ArtistDto>>
    suspend fun getSongsByArtist(artist: String): Result<List<Song>>
}