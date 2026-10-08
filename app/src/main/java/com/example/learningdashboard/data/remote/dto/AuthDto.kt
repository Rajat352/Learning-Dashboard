package com.example.learningdashboard.data.remote.dto

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val userId: Long,
    val name: String,
    val email: String,
    val accessToken: String
)
