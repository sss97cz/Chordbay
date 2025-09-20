package com.example.chords2.data.repository


import com.example.chords2.data.mappers.toDto
import com.example.chords2.data.mappers.toSong
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.SongUi
import com.example.chords2.data.remote.ChordsBayApiService
import com.example.chords2.data.remote.SongDto
import java.io.IOException

class SongRemoteRepositoryImpl(
    private val apiService: ChordsBayApiService
) : SongRemoteRepository {
    override suspend fun getSongs(): Result<List<Song>> {
        return try {
            val response = apiService.getPosts()
            if (response.isSuccessful) {
                val songs = response.body()
                if (songs != null) {
                    Result.success(songs.map { it.toSong() })
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch songs"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongById(id: String): Result<Song> {
        return try {
            val response = apiService.getPostById(id)
            if (response.isSuccessful) {
                val song = response.body()
                if (song != null) {
                    Result.success(song.toSong())
                } else {
                    Result.failure(IOException("Empty response body"))
                }
            } else {
                Result.failure(IOException("Failed to fetch song"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSong(song: Song): Result<Boolean> {
        return try {
            val response = apiService.createPost(song.toDto())
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(IOException("Failed to create song"))
            }
            } catch (e: Exception) {
            Result.failure(e)
        }
    }
}