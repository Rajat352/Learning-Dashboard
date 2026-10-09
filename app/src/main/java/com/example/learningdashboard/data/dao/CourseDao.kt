package com.example.learningdashboard.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.learningdashboard.data.dto.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY id")
    fun observeCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("UPDATE courses SET title = :title, instructor = :instructor, lessonCount = :lessonCount, completedLessonCount = MIN(completedLessonCount, :lessonCount) WHERE id = :id")
    suspend fun updateCourseMetadata(id: Long, title: String, instructor: String, lessonCount: Int)

    @Query("DELETE FROM courses WHERE id NOT IN (:ids)")
    suspend fun deleteMissingCourses(ids: List<Long>)

    @Query("DELETE FROM courses")
    suspend fun clearCourses()

    @Transaction
    suspend fun refreshCourses(courses: List<CourseEntity>) {
        if (courses.isEmpty()) {
            clearCourses()
        } else {
            insertCourses(courses)
            courses.forEach {
                updateCourseMetadata(it.id, it.title, it.instructor, it.lessonCount)
            }
            deleteMissingCourses(courses.map { it.id })
        }
    }
}
