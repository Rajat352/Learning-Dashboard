package com.example.learningdashboard.domain.model

data class Lesson(
    val id: Long,
    val courseId: Long,
    val title: String,
    val completed: Boolean
)

data class CourseDetails(
    val course: Course,
    val lessons: List<Lesson>,
    val isLessonsCached: Boolean
)

sealed interface CourseDetailsResult {
    data object Success : CourseDetailsResult
    data object Offline : CourseDetailsResult
    data object ServiceUnavailable : CourseDetailsResult
    data object PersistenceFailure : CourseDetailsResult
    data object NotFound : CourseDetailsResult
}
