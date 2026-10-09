package com.example.learningdashboard.data.repository

import android.app.Application
import androidx.room3.Room
import com.example.learningdashboard.data.AppDatabase
import com.example.learningdashboard.data.dto.CourseEntity
import com.example.learningdashboard.data.remote.api.LessonsApi
import com.example.learningdashboard.data.remote.dto.LessonDto
import com.example.learningdashboard.domain.model.CourseDetailsResult
import com.example.learningdashboard.domain.usecase.CalculateCourseProgressUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class CourseDetailsRepositoryImplTest {
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test(timeout = 20_000)
    fun completingLesson_updatesCourseSummary_andSurvivesRefresh() = runBlocking {
        // Create one course and two incomplete lessons
        val courseId = 1L
        val lessonId = 101L
        val remoteLessons = listOf(
            LessonDto(
                id = lessonId,
                courseId = courseId,
                title = "Introduction",
                completed = false,
                position = 0
            ),
            LessonDto(
                id = 102L,
                courseId = courseId,
                title = "Variables",
                completed = false,
                position = 1
            )
        )
        // Fake api always returning the same course data
        val api = object : LessonsApi {
            override suspend fun getLessons(courseId: Long): List<LessonDto> {
                assertEquals("The API should receive our course ID", 1L, courseId)
                return remoteLessons
            }
        }
        val repository = CourseDetailsRepositoryImpl(
            api = api,
            lessonDao = database.lessonDao()
        )
        val calculateProgress = CalculateCourseProgressUseCase()
        database.courseDao().insertCourses(
            listOf(
                CourseEntity(
                    id = courseId,
                    title = "Kotlin Basics",
                    instructor = "Test Instructor",
                    lessonCount = 2,
                    completedLessonCount = 0
                )
            )
        )
        // Seed lessons through the same refresh path the app uses, and require success
        assertEquals(CourseDetailsResult.Success, repository.refreshLessons(courseId))
        val initial = requireNotNull(repository.observeCourseDetails(courseId).first())
        // requireNotNull fails if the course is missing; first reads its current persisted state
        assertEquals(2, initial.lessons.size)
        assertTrue(initial.lessons.none { it.completed })
        assertEquals(0, initial.course.completedLessonCount)

        // Complete a lesson
        assertEquals(CourseDetailsResult.Success, repository.markLessonComplete(courseId, lessonId))

        // Read persisted data rather than trusting the operation's success result alone
        val completed = requireNotNull(repository.observeCourseDetails(courseId).first())
        assertTrue(completed.lessons.single { it.id == lessonId }.completed)
        assertFalse(completed.lessons.single { it.id == 102L }.completed)
        assertEquals(2, completed.course.lessonCount)
        assertEquals(1, completed.course.completedLessonCount)
        assertEquals(50, calculateProgress(completed.course.lessonCount, completed.course.completedLessonCount))

        // Refresh using stale server data where both lessons are still incomplete
        assertEquals(CourseDetailsResult.Success, repository.refreshLessons(courseId))

        // Refreshing preserves the learner's locally saved progress
        val refreshed = requireNotNull(repository.observeCourseDetails(courseId).first())
        assertTrue(refreshed.lessons.single { it.id == lessonId }.completed)
        assertFalse(refreshed.lessons.single { it.id == 102L }.completed)
        assertEquals(2, refreshed.course.lessonCount)
        assertEquals(1, refreshed.course.completedLessonCount)
        assertEquals(50, calculateProgress(refreshed.course.lessonCount, refreshed.course.completedLessonCount))
    }
}
