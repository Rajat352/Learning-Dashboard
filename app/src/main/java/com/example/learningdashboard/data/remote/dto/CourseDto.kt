package com.example.learningdashboard.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedLessonCount: Int
)
