package com.example.learningdashboard.domain.repository

import com.example.learningdashboard.domain.model.LoginResult
import com.example.learningdashboard.domain.model.SessionUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessionUser: Flow<SessionUser?>
    suspend fun login(email: String, password: String): LoginResult
    suspend fun logout()
}
