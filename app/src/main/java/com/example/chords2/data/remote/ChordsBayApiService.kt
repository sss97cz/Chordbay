package com.example.chords2.data.remote

import com.example.chords2.data.remote.model.ArtistDto
import com.example.chords2.data.remote.model.SongDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ChordsBayApiService {
    @GET("/api/songs")
    suspend fun getSongs(): Response<List<SongDto>>

    @GET("/api/songs/{id}")
    suspend fun getSongById(@Path("id") id: String): Response<SongDto>

    @POST("/api/songs")
    suspend fun createSong(
        @Header("Authorization")
        token: String,
        @Body songDto: SongDto
    ): Response<String> // Returns the ID of the created song

    @PUT("/api/songs/{id}")
    suspend fun updateSong(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Body songDto: SongDto
    ): Response<Boolean>

    @GET("/api/artists")
    suspend fun getAllArtists(): Response<List<ArtistDto>>

    @GET("/api/songs")
    suspend fun getSongsByArtist(@Query("artist") artist: String): Response<List<SongDto>>

}