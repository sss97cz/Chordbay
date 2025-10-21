package com.example.chords2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.datastore.UserDataStore
import com.example.chords2.data.remote.model.AuthRequest
import com.example.chords2.data.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userDataStore: UserDataStore
): ViewModel() {
    //-------------------- User Authentication states ----------------------------------------------
    init {
        viewModelScope.launch {
            userDataStore.getUsername().collect { email ->
                if (email.isNotEmpty()) {
                    setUserLoggedIn(true)
                    setUserEmail(email)
                    Log.d("AuthViewModel", "Loaded saved username: $email")
                } else {
                    setUserLoggedIn(false)
                    setUserEmail(null)
                }
            }
        }
    }
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()
    fun setUserLoggedIn(loggedIn: Boolean) {
        _isUserLoggedIn.value = loggedIn
    }
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


    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(AuthRequest(email, password))
                .onSuccess {
                    setUserLoggedIn(true)
                    setUserEmail(email)
                    userDataStore.saveUsername(email)
                }
                .onFailure { exception ->
                    _error.value = "Login failed: ${exception.message}"
                }
        }
    }
    fun logoutUser() {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess {
                    setUserLoggedIn(false)
                    setUserEmail(null)
                    userDataStore.saveUsername("")
                }
                .onFailure { exception ->
                    _error.value = "Logout failed: ${exception.message}"
                }
        }
    }
    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.register(AuthRequest(email, password))
                .onSuccess {
                    // Registration successful, you might want to log the user in or notify them
                }
                .onFailure { exception ->
                    _error.value = "Registration failed: ${exception.message}"
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
}