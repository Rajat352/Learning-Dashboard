package com.example.learningdashboard.ui.screens.coursedetails.state

import androidx.compose.runtime.Stable
import com.example.learningdashboard.domain.model.UiText

@Stable
data class CourseDetailsScreenState(
    val course: CourseDetailsUiState? = null,
    val lessons: List<LessonUiState> = emptyList(),
    val isLoading: Boolean = true,
    val isCacheLoaded: Boolean = false,
    val isLessonsCached: Boolean = false,
    val updatingLessonIds: Set<Long> = emptySet(),
    val error: UiText? = null,
    val completionError: UiText? = null
)

@Stable
data class CourseDetailsUiState(
    val title: String,
    val lessonCount: Int,
    val completedLessonCount: Int,
    val progress: Int
)

@Stable
data class LessonUiState(
    val id: Long,
    val title: String,
    val completed: Boolean
)
