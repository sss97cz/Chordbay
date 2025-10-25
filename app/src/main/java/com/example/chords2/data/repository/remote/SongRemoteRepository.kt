package com.example.chords2.data.repository.remote

import com.example.chords2.data.model.Song
import com.example.chords2.data.remote.model.ArtistDto

interface SongRemoteRepository {
    suspend fun getSongs(): Result<List<Song>>
    suspend fun getSongById(id: String, token: String?): Result<Song>
    suspend fun createSong(song: Song, token: String): Result<String>
    suspend fun updateSong(song: Song, token: String): Result<Boolean>
    suspend fun getAllArtists(): Result<List<ArtistDto>>
    suspend fun getSongsByArtist(artist: String): Result<List<Song>>
    suspend fun getMySongs(token: String): Result<List<Song>>
}