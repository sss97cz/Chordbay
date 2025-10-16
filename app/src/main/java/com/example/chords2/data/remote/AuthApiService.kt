package com.example.chords2.data.remote

import com.example.chords2.data.model.TokenPair
import com.example.chords2.data.remote.model.AuthRequest
import com.example.chords2.data.remote.model.RefreshRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body body: AuthRequest): Response<Unit>

    @POST("/api/auth/login")
    suspend fun login(@Body body: AuthRequest): Response<TokenPair>

    @POST("/api/auth/refresh")
    suspend fun refresh(@Body body: RefreshRequest): Response<TokenPair>

    @POST("/api/auth/logout")
    suspend fun logout(@Body body: RefreshRequest): Response<Unit>
}
