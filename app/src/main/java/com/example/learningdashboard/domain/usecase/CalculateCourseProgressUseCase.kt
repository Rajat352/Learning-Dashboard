package com.example.learningdashboard.domain.usecase

import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateCourseProgressUseCase @Inject constructor() {
    operator fun invoke(lessonCount: Int, completedLessonCount: Int): Int {
        if (lessonCount <= 0) return 0
        val completed = completedLessonCount.coerceIn(0, lessonCount)
        return (completed.toDouble() / lessonCount * 100).roundToInt()
    }
}
