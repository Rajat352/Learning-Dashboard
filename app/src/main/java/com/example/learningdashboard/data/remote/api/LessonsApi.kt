package com.example.learningdashboard.data.remote.api

import com.example.learningdashboard.data.remote.MockApiTransport
import com.example.learningdashboard.data.remote.MockServerException
import com.example.learningdashboard.data.remote.dto.LessonDto
import java.io.IOException

interface LessonsApi {
    suspend fun getLessons(courseId: Long): List<LessonDto>
}

enum class LessonsScenario { NORMAL, EMPTY, SERVER_ERROR }

class FakeLessonsApiImpl(
    private val transport: MockApiTransport,
    private val scenario: LessonsScenario = LessonsScenario.NORMAL
) : LessonsApi {
    override suspend fun getLessons(courseId: Long): List<LessonDto> = transport.execute {
        if (scenario == LessonsScenario.SERVER_ERROR) throw MockServerException()
        val fixture = when (courseId) {
            1L -> PYTHON_LESSONS to 13
            2L -> AI_LESSONS to 6
            3L -> FULL_STACK_LESSONS to 7
            else -> throw CourseNotFoundException()
        }
        if (scenario == LessonsScenario.EMPTY) return@execute emptyList()
        fixture.first.mapIndexed { index, title ->
            LessonDto(
                id = courseId * 1000 + index + 1,
                courseId = courseId,
                title = title,
                completed = index < fixture.second,
                position = index
            )
        }
    }

    companion object {
        private val PYTHON_LESSONS = listOf(
            "Introduction", "Setting Up Python", "Variables & Data Types", "Operators",
            "Strings", "Lists", "Tuples", "Dictionaries", "Sets", "Conditionals",
            "Loops", "Functions", "Modules", "File Handling", "Exceptions",
            "Object-Oriented Programming", "Inheritance", "Virtual Environments",
            "Testing", "Final Project"
        )
        private val AI_LESSONS = listOf(
            "Introduction to Generative AI", "Machine Learning Basics", "Neural Networks",
            "Transformers", "Large Language Models", "Prompt Basics", "Prompt Patterns",
            "Embeddings", "Vector Databases", "Retrieval-Augmented Generation",
            "Fine-Tuning", "Image Generation", "Evaluation", "Responsible AI",
            "Deployment", "Final Project"
        )
        private val FULL_STACK_LESSONS = listOf(
            "Introduction", "Development Setup", "HTML Basics", "Semantic HTML",
            "CSS Basics", "Layouts", "Responsive Design", "JavaScript Basics",
            "Functions", "Arrays & Objects", "DOM Manipulation", "Asynchronous JavaScript",
            "HTTP", "REST APIs", "Frontend Components", "State Management",
            "Routing", "Forms", "Backend Basics", "Database Basics", "Data Models",
            "Authentication", "Authorization", "Validation", "Testing",
            "Deployment", "Monitoring", "Final Project"
        )
    }
}

class CourseNotFoundException : IOException()
