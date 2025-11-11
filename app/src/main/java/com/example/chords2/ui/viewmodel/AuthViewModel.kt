package com.example.chords2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.datastore.UserDataStore
import com.example.chords2.data.remote.model.AuthRequest
import com.example.chords2.data.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userDataStore: UserDataStore,
) : ViewModel() {
    //-------------------- User Authentication states ----------------------------------------------
    init {
        viewModelScope.launch {
            userDataStore.getUsername().collect { email ->
                if (email.isNotEmpty()) {
                    setUserEmail(email)
                    Log.d("AuthViewModel", "Loaded saved username: $email")
                } else {
                    setUserEmail(null)
                }
            }
        }
    }
    private val _logoutEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logoutEvents = _logoutEvents.asSharedFlow()

    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isUserLoggedIn
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()
    fun setUserEmail(email: String?) {
        _userEmail.value = email
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    fun clearError() {
        _error.value = null
    }

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun loginUser(email: String, password: String) {
        _registerSuccess.value = false
        _loading.value = true
        viewModelScope.launch {
            authRepository.login(AuthRequest(email, password))
                .onSuccess {
                    setUserEmail(email)
                    userDataStore.saveUsername(email)
                    _loading.value = false
                }
                .onFailure { exception ->
                    _error.value = "Login failed: ${exception.message}"
                    _loading.value = false
                }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    setUserEmail(null)
                    userDataStore.saveUsername("")
                    Log.d("AuthViewModel", "Cleared saved username on logout")
                    _logoutEvents.tryEmit(Unit)
                    Log.d("AuthViewModel", "User logged out successfully")
                }
                .onFailure { exception ->
                    _error.value = "Logout failed: ${exception.message}"
                }
        }
    }

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess = _registerSuccess.asStateFlow()
    fun setRegisterSuccess(success: Boolean) {
        _registerSuccess.value = success
    }
    fun registerUser(email: String, password: String) {
        _loading.value = true
        viewModelScope.launch {
            authRepository.register(AuthRequest(email, password))
                .onSuccess {
                    Log.d("AuthViewModel", "Registration successful for user: $email")
                    _registerSuccess.value = true
                    _loading.value = false
                }
                .onFailure { exception ->
                    _error.value = "Registration failed: ${exception.message}"
                    _loading.value = false
                }
        }
    }

    fun refreshToken() {
        viewModelScope.launch {
            authRepository.refresh()
                .onSuccess {
                    // Token refreshed successfully
                }
                .onFailure { exception ->
                    _error.value = "Token refresh failed: ${exception.message}"
                }
        }
    }


    fun resendVerificationEmail(email: String) {
        _loading.value = true
        viewModelScope.launch {
            authRepository.resensdVerificationEmail(email)
                .onSuccess {
                    // Verification email resent successfully
                    _loading.value = false
                }
                .onFailure { exception ->
                    _error.value = "Resend verification email failed: ${exception.message}"
                    _loading.value = false
                }
        }
    }
}