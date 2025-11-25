package com.example.chords2.data.remote.model

data class ChangePasswordRequest(
    val email: String,
    val currentPassword: String,
    val newPassword: String
)
