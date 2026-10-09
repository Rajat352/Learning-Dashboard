package com.example.learningdashboard.data.dto

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [ForeignKey(CourseEntity::class, ["id"], ["courseId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("courseId")]
)
data class LessonsEntity(
    @PrimaryKey val id: Long,
    val courseId: Long,
    val title: String,
    val completed: Boolean,
    val position: Int
)