package com.chordbay.app.data.remote

import com.chordbay.app.data.remote.model.ArtistDto
import com.chordbay.app.data.remote.model.NotificationDto
import com.chordbay.app.data.remote.model.SongDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    suspend fun getSongById(
        @Header("Authorization")
        token: String,
        @Path("id") id: String,
    ): Response<SongDto>

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

    @GET("/api/songs/me")
    suspend fun getMySongs(
        @Header("Authorization")
        token: String,
    ): Response<List<SongDto>>

    @DELETE ("/api/songs/{id}")
    suspend fun deleteSong(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Response<Boolean>


    @GET("/api/songs/search")
    suspend fun searchSongs(
        @Query("query") query: String? = null,
        // "title" | "artist" | "both"
        @Query("field") field: String? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
    ): Response<List<SongDto>>

    @GET("/api/songs/most-viewed")
    suspend fun getSongsByViewedCount(
        @Query("limit") limit: Int = 25
    ): Response<List<SongDto>>

    @GET("/api/notifications")
    suspend fun getNotifications(
    ): Response<List<NotificationDto>>
}