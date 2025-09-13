package com.example.chords2.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface JsonPlaceholderApiService {
    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Response<PostDto>
    @POST("posts")
    suspend fun createPost(@Body postDto: PostDto): Response<PostDto>
}