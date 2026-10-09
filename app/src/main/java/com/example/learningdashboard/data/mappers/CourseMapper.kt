package com.example.learningdashboard.data.mappers

import com.example.learningdashboard.data.dto.CourseEntity
import com.example.learningdashboard.data.remote.dto.CourseDto
import com.example.learningdashboard.domain.model.Course

fun CourseEntity.toDomain(): Course = Course(
    id = id,
    title = title,
    instructor = instructor,
    lessonCount = lessonCount,
    completedLessonCount = completedLessonCount
)

fun CourseDto.toEntity(): CourseEntity = CourseEntity(
    id = id,
    title = title,
    instructor = instructor,
    lessonCount = lessonCount.coerceAtLeast(0),
    completedLessonCount = completedLessonCount.coerceIn(0, lessonCount.coerceAtLeast(0))
)
