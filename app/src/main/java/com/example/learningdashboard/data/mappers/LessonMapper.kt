package com.example.learningdashboard.data.mappers

import com.example.learningdashboard.data.dto.CourseWithLessonsEntity
import com.example.learningdashboard.data.dto.LessonsEntity
import com.example.learningdashboard.data.remote.dto.LessonDto
import com.example.learningdashboard.domain.model.CourseDetails
import com.example.learningdashboard.domain.model.Lesson

fun LessonsEntity.toDomain(): Lesson = Lesson(
    id = id,
    courseId = courseId,
    title = title,
    completed = completed
)

fun LessonDto.toEntity(): LessonsEntity = LessonsEntity(
    id = id,
    courseId = courseId,
    title = title,
    completed = completed,
    position = position
)

fun CourseWithLessonsEntity.toDomain(): CourseDetails = CourseDetails(
    course = course.toDomain(),
    lessons = lessons.sortedBy { it.position }.map { it.toDomain() },
    isLessonsCached = course.isLessonsCached
)
