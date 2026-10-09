package com.example.learningdashboard.ui.screens.coursedetails.action

sealed interface CourseDetailsScreenAction {
    data class LoadCourse(val courseId: Long) : CourseDetailsScreenAction
    data object Refresh : CourseDetailsScreenAction
    data class MarkLessonComplete(val lessonId: Long) : CourseDetailsScreenAction
}
