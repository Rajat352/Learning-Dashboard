package com.example.learningdashboard.ui.screens.courses.state

import androidx.compose.runtime.Stable
import com.example.learningdashboard.domain.model.UiText

@Stable
data class CoursesScreenState(
    val courses: List<CourseUiState> = emptyList(),
    val isLoading: Boolean = true,
    val isCacheLoaded: Boolean = false,
    val isLoggingOut: Boolean = false,
    val error: UiText? = null
)

@Stable
data class CourseUiState(
    val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val progress: Int
)
