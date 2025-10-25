package com.example.chords2.data.repository.auth

import com.example.chords2.data.model.TokenPair
import com.example.chords2.data.remote.model.AuthRequest
import com.example.chords2.data.remote.model.RefreshRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val isUserLoggedIn: StateFlow<Boolean>
    suspend fun login(authRequest: AuthRequest): Result<Unit>
    suspend fun register(authRequest: AuthRequest): Result<Unit>
    suspend fun refresh(): Result<Unit>
    suspend fun logout(): Result<Unit>

    suspend fun getAcessToken(): String?
}