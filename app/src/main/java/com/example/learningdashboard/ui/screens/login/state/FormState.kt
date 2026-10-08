package com.example.learningdashboard.ui.screens.login.state

import com.example.learningdashboard.domain.model.UiText

data class FormState(
    val email: String = "",
    val password: String = "",

    val emailError: UiText? = null,
    val passwordError: UiText? = null,
)
