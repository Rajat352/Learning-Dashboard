package com.example.learningdashboard.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.learningdashboard.data.dto.CourseEntity
import com.example.learningdashboard.data.dto.LessonsEntity
import com.example.learningdashboard.data.dto.UserEntity

@Database(
    entities = [UserEntity::class, CourseEntity::class, LessonsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
}