package com.example.learningdashboard.data.remote.api

import com.example.learningdashboard.data.remote.MockApiTransport
import com.example.learningdashboard.data.remote.MockServerException
import com.example.learningdashboard.data.remote.dto.LoginRequestDto
import com.example.learningdashboard.data.remote.dto.LoginResponseDto

interface AuthApi {
    suspend fun login(request: LoginRequestDto): LoginResponseDto
}

enum class LoginScenario { NORMAL, SERVER_ERROR }

class FakeAuthApiImpl(
    private val transport: MockApiTransport,
    private val scenario: LoginScenario = LoginScenario.NORMAL
) : AuthApi {
    override suspend fun login(request: LoginRequestDto): LoginResponseDto = transport.execute {
        if (scenario == LoginScenario.SERVER_ERROR) throw MockServerException()
        if (request.email != "demo@example.com" || request.password != "Demo1234") {
            throw InvalidCredentialsException()
        }
        LoginResponseDto(
            userId = 1L,
            name = "Demo User",
            email = "demo@example.com",
            accessToken = "mock-access-token"
        )
    }
}

class InvalidCredentialsException : Exception()