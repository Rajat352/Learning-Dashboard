package com.example.learningdashboard.ui.screens.login

sealed interface LoginScreenAction {
    data class EmailChanged(val email: String): LoginScreenAction
    data class PasswordChanged(val pass: String): LoginScreenAction
    data class PasswordVisibilityToggle(val isVisible: Boolean): LoginScreenAction
    data object Submit: LoginScreenAction
}