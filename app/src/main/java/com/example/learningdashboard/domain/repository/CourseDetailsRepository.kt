package com.example.learningdashboard.domain.repository

import com.example.learningdashboard.domain.model.CourseDetails
import com.example.learningdashboard.domain.model.CourseDetailsResult
import kotlinx.coroutines.flow.Flow

interface CourseDetailsRepository {
    fun observeCourseDetails(courseId: Long): Flow<CourseDetails?>
    suspend fun refreshLessons(courseId: Long): CourseDetailsResult
    suspend fun markLessonComplete(courseId: Long, lessonId: Long): CourseDetailsResult
}
