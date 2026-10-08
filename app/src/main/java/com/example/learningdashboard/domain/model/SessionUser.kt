package com.example.learningdashboard.domain.model

data class SessionUser(
    val userId: Long,
    val name: String,
    val email: String
)

sealed interface LoginResult {
    data object Success : LoginResult
    data object InvalidCredentials : LoginResult
    data object Offline : LoginResult
    data object ServiceUnavailable : LoginResult
    data object PersistenceFailure : LoginResult
}
