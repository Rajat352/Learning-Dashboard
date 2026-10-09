package com.example.learningdashboard.data.dto

import androidx.room3.Embedded
import androidx.room3.Relation

data class CourseWithLessonsEntity(
    @Embedded val course: CourseEntity,
    @Relation(parentColumns = ["id"], entityColumns = ["courseId"])
    val lessons: List<LessonsEntity>
)
