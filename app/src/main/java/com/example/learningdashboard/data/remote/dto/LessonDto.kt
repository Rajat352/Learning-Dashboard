package com.example.learningdashboard.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    val id: Long,
    val courseId: Long,
    val title: String,
    val completed: Boolean,
    val position: Int
)
