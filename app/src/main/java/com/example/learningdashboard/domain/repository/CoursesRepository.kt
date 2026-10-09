package com.example.learningdashboard.domain.repository

import com.example.learningdashboard.domain.model.Course
import com.example.learningdashboard.domain.model.CoursesResult
import kotlinx.coroutines.flow.Flow

interface CoursesRepository {
    val courses: Flow<List<Course>>
    suspend fun refreshCourses(): CoursesResult
}
