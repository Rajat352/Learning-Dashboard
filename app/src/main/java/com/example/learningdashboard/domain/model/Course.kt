package com.example.learningdashboard.domain.model

data class Course(
    val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedLessonCount: Int
)

sealed interface CoursesResult {
    data object Success : CoursesResult
    data object Offline : CoursesResult
    data object ServiceUnavailable : CoursesResult
    data object PersistenceFailure : CoursesResult
}
