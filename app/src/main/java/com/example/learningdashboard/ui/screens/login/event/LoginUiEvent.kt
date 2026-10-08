package com.example.learningdashboard.ui.screens.login

sealed interface LoginUiEvent {
    data object Success : LoginUiEvent
}
