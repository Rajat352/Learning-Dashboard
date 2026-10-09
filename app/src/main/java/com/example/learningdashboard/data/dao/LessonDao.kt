package com.example.learningdashboard.data.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.learningdashboard.data.dto.CourseWithLessonsEntity
import com.example.learningdashboard.data.dto.LessonsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun observeCourseDetails(courseId: Long): Flow<CourseWithLessonsEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM courses WHERE id = :courseId)")
    suspend fun courseExists(courseId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessons(lessons: List<LessonsEntity>)

    @Query("UPDATE lessons SET title = :title, position = :position WHERE id = :id AND courseId = :courseId")
    suspend fun updateLessonMetadata(courseId: Long, id: Long, title: String, position: Int)

    @Query("DELETE FROM lessons WHERE courseId = :courseId AND id NOT IN (:ids)")
    suspend fun deleteMissingLessons(courseId: Long, ids: List<Long>)

    @Query("DELETE FROM lessons WHERE courseId = :courseId")
    suspend fun clearLessons(courseId: Long)

    @Query("UPDATE courses SET lessonCount = :lessonCount, completedLessonCount = :completedLessonCount WHERE id = :courseId AND isLessonsCached = 0")
    suspend fun updateCourseSummary(courseId: Long, lessonCount: Int, completedLessonCount: Int)

    @Query("UPDATE courses SET lessonCount = (SELECT COUNT(*) FROM lessons WHERE courseId = :courseId), completedLessonCount = (SELECT COUNT(*) FROM lessons WHERE courseId = :courseId AND completed = 1), isLessonsCached = 1 WHERE id = :courseId")
    suspend fun updateCourseCounts(courseId: Long)

    @Query("UPDATE lessons SET completed = 1 WHERE courseId = :courseId AND id = :lessonId AND completed = 0")
    suspend fun completeLesson(courseId: Long, lessonId: Long): Int

    @Query("SELECT EXISTS(SELECT 1 FROM lessons WHERE courseId = :courseId AND id = :lessonId)")
    suspend fun lessonExists(courseId: Long, lessonId: Long): Boolean

    @Transaction
    suspend fun refreshLessons(courseId: Long, lessons: List<LessonsEntity>): Boolean {
        if (!courseExists(courseId)) return false
        require(lessons.all { it.courseId == courseId })
        if (lessons.isEmpty()) {
            clearLessons(courseId)
        } else {
            insertLessons(lessons)
            lessons.forEach {
                updateLessonMetadata(courseId, it.id, it.title, it.position)
            }
            deleteMissingLessons(courseId, lessons.map { it.id })
        }
        updateCourseCounts(courseId)
        return true
    }

    @Transaction
    suspend fun markLessonComplete(courseId: Long, lessonId: Long): Boolean {
        val updated = completeLesson(courseId, lessonId)
        if (updated == 0 && !lessonExists(courseId, lessonId)) return false
        updateCourseCounts(courseId)
        return true
    }
}
