package com.example.learningdashboard.ui.state

import com.example.learningdashboard.domain.model.UiText

data class RefreshState(
    val isLoading: Boolean = true,
    val error: UiText? = null
)
