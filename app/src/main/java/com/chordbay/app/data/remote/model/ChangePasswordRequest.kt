package com.chordbay.app.data.remote.model

data class ChangePasswordRequest(
    val email: String,
    val currentPassword: String,
    val newPassword: String
)
