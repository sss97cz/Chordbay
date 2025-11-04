package com.example.chords2.data.repository.remote

import android.util.Log
import com.example.chords2.data.mappers.toDto
import com.example.chords2.data.mappers.toSong
import com.example.chords2.data.model.Song
import com.example.chords2.data.remote.ChordsBayApiService
import com.example.chords2.data.remote.model.ArtistDto
import com.example.chords2.ui.composable.screen.FilterField
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

    override suspend fun getSongById(id: String, token: String?): Result<Song> {
        return try {
            val response = apiService.getSongById(
                token = "Bearer $token",
                id = id,
            )
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

    override suspend fun getMySongs(token: String): Result<List<Song>> {
        return try {
            val response = apiService.getMySongs(
                token = "Bearer $token",
            )
            if (response.isSuccessful) {
                val songs = response.body()
                if (songs != null) {
                    Result.success(songs.map { it.toSong() })
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch my songs, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSong(id: String, token: String): Result<Boolean> {
        return try {
            val response = apiService.deleteSong(
                id = id,
                token = "Bearer $token"
            )
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(IOException("Failed to delete song, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchSongs(
        query: String?,
        field: FilterField,
        offset: Int,
        limit: Int
    ): Result<List<Song>> {
        return try {
            val response = apiService.searchSongs(
                query = query,
                field = when (field) {
                    FilterField.TITLE -> "title"
                    FilterField.ARTIST -> "artist"
                    FilterField.BOTH -> "both"
                    else -> {"both"}
                },
                offset = offset,
                limit = limit
            )
            if (response.isSuccessful) {
                val songs = response.body()
                if (songs != null) Result.success(songs.map { it.toSong() })
                else Result.failure(IOException("Empty response body"))
            } else {
                Result.failure(IOException("Failed to search songs, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongsByViewedCount(): Result<List<Song>> {
        return try {
            val response = apiService.getSongsByViewedCount()
            if (response.isSuccessful) {
                val songs = response.body()
                if (songs != null) {
                    Result.success(songs.map { it.toSong() })
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch songs by viewed count, code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}