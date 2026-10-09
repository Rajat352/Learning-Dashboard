package com.example.learningdashboard.data.remote.api

import com.example.learningdashboard.data.remote.MockApiTransport
import com.example.learningdashboard.data.remote.MockServerException
import com.example.learningdashboard.data.remote.dto.CourseDto
import kotlinx.serialization.json.Json

interface CoursesApi {
    suspend fun getCourses(): List<CourseDto>
}

enum class CoursesScenario { NORMAL, EMPTY, SERVER_ERROR }

class FakeCoursesApiImpl(
    private val transport: MockApiTransport,
    private val scenario: CoursesScenario = CoursesScenario.NORMAL
) : CoursesApi {
    override suspend fun getCourses(): List<CourseDto> = transport.execute {
        when (scenario) {
            CoursesScenario.NORMAL -> Json.decodeFromString<List<CourseDto>>(COURSES_JSON)
            CoursesScenario.EMPTY -> emptyList()
            CoursesScenario.SERVER_ERROR -> throw MockServerException()
        }
    }

    companion object {
        private val COURSES_JSON = """
            [
                {
                    "id": 1,
                    "title": "Python Programming",
                    "instructor": "John Smith",
                    "lessonCount": 20,
                    "completedLessonCount": 13
                },
                {
                    "id": 2,
                    "title": "Generative AI",
                    "instructor": "Sarah Williams",
                    "lessonCount": 16,
                    "completedLessonCount": 6
                },
                {
                    "id": 3,
                    "title": "Full Stack Development",
                    "instructor": "David Brown",
                    "lessonCount": 28,
                    "completedLessonCount": 7
                }
            ]
        """.trimIndent()
    }
}
