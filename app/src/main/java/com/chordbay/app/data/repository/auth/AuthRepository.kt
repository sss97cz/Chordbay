package com.chordbay.app.data.repository.auth

import com.chordbay.app.data.remote.model.AuthRequest
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    val isUserLoggedIn: StateFlow<Boolean>
    suspend fun login(authRequest: AuthRequest): Result<Unit>
    suspend fun register(authRequest: AuthRequest): Result<Unit>
    suspend fun refresh(): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun getAccessToken(): String?

    suspend fun resendVerificationEmail(email: String): Result<Unit>

    fun setIsUserLoggedIn(loggedIn: Boolean)

    suspend fun deleteAccount(): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun changePassword(email: String, newPassword: String, oldPassword: String): Result<Unit>
}