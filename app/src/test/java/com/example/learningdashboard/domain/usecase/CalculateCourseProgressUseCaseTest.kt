package com.example.learningdashboard.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateCourseProgressUseCaseTest {
    @Test
    fun twoOfThreeLessonsCompleted_returnsRoundedProgressOf67Percent() {
        val calculateProgress = CalculateCourseProgressUseCase()

        val progress = calculateProgress(lessonCount = 3, completedLessonCount = 2)

        assertEquals(67, progress)
    }
}
