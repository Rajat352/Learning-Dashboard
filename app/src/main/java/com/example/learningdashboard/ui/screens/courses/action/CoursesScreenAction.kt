package com.example.learningdashboard.ui.screens.courses.action

sealed interface CoursesScreenAction {
    data object Refresh : CoursesScreenAction
}
