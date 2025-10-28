package com.example.chords2.data.repository.auth

import com.example.chords2.data.datastore.CredentialManager
import com.example.chords2.data.datastore.UserDataStore
import com.example.chords2.data.model.TokenPair
import com.example.chords2.data.remote.AuthApiService
import com.example.chords2.data.remote.model.AuthRequest
import com.example.chords2.data.remote.model.RefreshRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val credentialManager: CredentialManager,
): AuthRepository {

    private val _isUserLoggedIn = MutableStateFlow(false)
    override val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    override suspend fun login(authRequest: AuthRequest): Result<Unit> {
        return try {
            val response = authApiService.login(authRequest)
            if (response.isSuccessful) {
                val tokenPair = response.body()
                if (tokenPair != null) {
                    // Save tokens using CredentialManager
                    credentialManager.saveTokens(tokenPair)
                    _isUserLoggedIn.value = true
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Login failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(authRequest: AuthRequest): Result<Unit> {
        return try {
            val response = authApiService.register(authRequest)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registration failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refresh(): Result<Unit> {
        return try {
            val refreshToken = credentialManager.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token available"))
            val response = authApiService.refresh(RefreshRequest(refreshToken))
            if (response.isSuccessful) {
                val tokenPair = response.body()
                if (tokenPair != null) {
                    credentialManager.saveTokens(tokenPair)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                Result.failure(Exception("Token refresh failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val refreshRequest = credentialManager.getRefreshToken()
                ?: return Result.failure(Exception("No refresh token available"))
            val response = authApiService.logout(RefreshRequest(refreshRequest))
            if (response.isSuccessful) {
                // Clear tokens from CredentialManager
                credentialManager.clearTokens()
                _isUserLoggedIn.value = false
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun resensdVerificationEmail(email: String): Result<Unit> {
        return try {
            val response = authApiService.resendVerificationEmail(
                com.example.chords2.data.remote.model.ResendRequest(email)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Resend verification email failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAccessToken(): String? =
        credentialManager.getAccessToken()

}