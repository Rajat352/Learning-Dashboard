package com.example.learningdashboard.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route: NavKey {
    @Serializable data class CourseDetails(val courseId: Long): Route

    sealed interface TopLevel: Route {
        @Serializable data object Login: TopLevel
        @Serializable data object Courses: TopLevel
    }
}