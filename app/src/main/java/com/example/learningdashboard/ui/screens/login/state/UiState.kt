package com.example.learningdashboard.ui.screens.login.state

import com.example.learningdashboard.domain.model.UiText

data class UiState(
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null
)
