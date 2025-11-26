package com.chordbay.app.data.model.util

enum class Error(val message: String) {
    NO_INTERNET("No internet connection available."),
    NOT_LOGGED_IN("User is not logged in."),
    LOGIN_FAILED_INVALID_CREDENTIALS("Login attempt failed. Invalid email or password."),
    REGISTRATION_FAILED("Registration attempt failed. This email may already be in use."),
    PERMISSION_DENIED("Permission denied."),
    SYNC_FAILED("Data synchronization failed."),
    UNKNOWN_ERROR("An unknown error occurred."),
    LOGOUT_FAILED("Logout attempt failed."),
    RESET_PASSWORD_FAILED("Password reset attempt failed."),
    CHANGE_PASSWORD_FAILED("Change password attempt failed.")
}
fun String.toError(): Error {
    val hasCode: Boolean = this.contains("code:")
    if (hasCode){
        val code = this.substringAfter("code:").trim().toIntOrNull()
        return when (code) {
            401 -> Error.NOT_LOGGED_IN
            403 -> Error.PERMISSION_DENIED
            else -> Error.UNKNOWN_ERROR
        }
    } else {
        return when {
            this.contains("Unable to resolve host", ignoreCase = true) -> Error.NO_INTERNET
            this.contains("Sync failed", ignoreCase = true) -> Error.SYNC_FAILED
            else -> Error.UNKNOWN_ERROR
        }
    }
}
fun String.toAuthError(): Error {
    return when {
        this.contains("Unable to resolve host", ignoreCase = true) -> Error.NO_INTERNET
        this.contains("Login", ignoreCase = true) -> Error.LOGIN_FAILED_INVALID_CREDENTIALS
        this.contains("Registration", ignoreCase = true) -> Error.REGISTRATION_FAILED
        this.contains("Logout", ignoreCase = true) -> Error.LOGOUT_FAILED
        this.contains("Forgot password") -> Error.RESET_PASSWORD_FAILED
        this.contains("Change password") -> Error.CHANGE_PASSWORD_FAILED
        else -> Error.UNKNOWN_ERROR
    }
}