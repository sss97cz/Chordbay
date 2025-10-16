package com.example.chords2.data.repository.auth

import com.example.chords2.data.datastore.CredentialManager
import com.example.chords2.data.datastore.UserDataStore
import com.example.chords2.data.model.TokenPair
import com.example.chords2.data.remote.AuthApiService
import com.example.chords2.data.remote.model.AuthRequest
import com.example.chords2.data.remote.model.RefreshRequest

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val credentialManager: CredentialManager,
): AuthRepository {
    override suspend fun login(authRequest: AuthRequest): Result<Unit> {
        return try {
            val response = authApiService.login(authRequest)
            if (response.isSuccessful) {
                val tokenPair = response.body()
                if (tokenPair != null) {
                    // Save tokens using CredentialManager
                    credentialManager.saveTokens(tokenPair)
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
                Result.success(Unit)
            } else {
                Result.failure(Exception("Logout failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAcessToken(): String? =
        credentialManager.getAccessToken()



}