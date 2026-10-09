package com.example.learningdashboard.data.dto

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedLessonCount: Int
)
