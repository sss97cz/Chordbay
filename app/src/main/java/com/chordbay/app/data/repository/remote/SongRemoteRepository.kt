package com.chordbay.app.data.repository.remote

import com.chordbay.app.data.model.Song
import com.chordbay.app.data.remote.model.ArtistDto
import com.chordbay.app.ui.composable.screen.FilterField

interface SongRemoteRepository {
    suspend fun getSongs(): Result<List<Song>>
    suspend fun getSongById(id: String, token: String?): Result<Song>
    suspend fun createSong(song: Song, token: String): Result<String>
    suspend fun updateSong(song: Song, token: String): Result<Boolean>
    suspend fun getAllArtists(): Result<List<ArtistDto>>
    suspend fun getSongsByArtist(artist: String): Result<List<Song>>
    suspend fun getMySongs(token: String): Result<List<Song>>
    suspend fun deleteSong(id: String, token: String): Result<Boolean>

    suspend fun searchSongs(
        query: String?,
        field: FilterField,
        offset: Int,
        limit: Int
    ): Result<List<Song>>

    suspend fun getSongsByViewedCount(): Result<List<Song>>
}