package com.example.chords2.data.repository.remote

import android.util.Log
import com.example.chords2.data.mappers.toDto
import com.example.chords2.data.mappers.toSong
import com.example.chords2.data.model.Song
import com.example.chords2.data.remote.ChordsBayApiService
import com.example.chords2.data.remote.model.ArtistDto
import java.io.IOException

class SongRemoteRepositoryImpl(
    private val apiService: ChordsBayApiService
) : SongRemoteRepository {
    override suspend fun getSongs(): Result<List<Song>> {
        return try {
            val response = apiService.getSongs()
            if (response.isSuccessful) {
                val songs = response.body()
                if (songs != null) {
                    Result.success(songs.map { it.toSong() })
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch songs, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongById(id: String): Result<Song> {
        return try {
            val response = apiService.getSongById(id)
            if (response.isSuccessful) {
                val song = response.body()
                if (song != null) {
                    Result.success(song.toSong())
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch song, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSong(song: Song, token: String): Result<String> {
        return try {
            val response = apiService.createSong(
                songDto = song.toDto(),
                token = "Bearer $token"
            )

            if (response.isSuccessful) {
                val songId = response.body()
                if (songId != null) {
                    Log.d("SongRemoteRepositoryImpl", "Song created with ID: $songId")
                    Result.success(songId)
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to create song, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllArtists(): Result<List<ArtistDto>> {
        return try {
            val response = apiService.getAllArtists()
            if (response.isSuccessful) {
                val artists = response.body()
                if (artists != null) {
                    Result.success(artists)
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch artists, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongsByArtist(artist: String): Result<List<Song>> {
        return try {
            val response = apiService.getSongsByArtist(artist)
            if (response.isSuccessful) {
                val songs = response.body()
                if (songs != null) {
                    Result.success(songs.map { it.toSong() })
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch songs by artist, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSong(song: Song, token: String): Result<Boolean> {
        return try {
            val response = apiService.updateSong(
                id = song.remoteId.toString(),
                token = "Bearer $token",
                songDto = song.toDto()
            )
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(IOException("Failed to update song, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}