package com.example.learningdashboard.data

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.learningdashboard.data.dao.CourseDao
import com.example.learningdashboard.data.dao.LessonDao
import com.example.learningdashboard.data.dao.SessionUserDao
import com.example.learningdashboard.data.dto.CourseEntity
import com.example.learningdashboard.data.dto.LessonsEntity
import com.example.learningdashboard.data.dto.SessionUserEntity

@Database(
    entities = [SessionUserEntity::class, CourseEntity::class, LessonsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun lessonDao(): LessonDao
    abstract fun courseDao(): CourseDao
    abstract fun sessionUserDao(): SessionUserDao
}