package com.example.chords2.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChordsBayApiService {
    @GET("/songs")
    suspend fun getPosts(): Response<List<SongDto>>
    @GET("/songs/{id}")
    suspend fun getPostById(@Path("id") id: String): Response<SongDto>
    @POST("/songs")
    suspend fun createPost(@Body songDto: SongDto): Response<SongDto>
}