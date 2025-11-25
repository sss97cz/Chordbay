package com.example.chords2.data.remote

import com.example.chords2.data.model.TokenPair
import com.example.chords2.data.remote.model.AuthRequest
import com.example.chords2.data.remote.model.ChangePasswordRequest
import com.example.chords2.data.remote.model.RefreshRequest
import com.example.chords2.data.remote.model.ResendRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
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

    @POST("/api/auth/verify/resend")
    suspend fun resendVerificationEmail(@Body body: ResendRequest): Response<Unit>

    @DELETE("/api/auth/me")
    suspend fun deleteAccount(
        @Header("Authorization")
        token: String
    ): Response<Unit>

    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgetPasswordRequest): Response<Unit>
    @POST("/api/auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest): Response<Unit>
}
