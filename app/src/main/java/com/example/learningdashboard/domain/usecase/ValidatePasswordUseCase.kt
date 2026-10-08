package com.example.learningdashboard.domain.usecase

import com.example.learningdashboard.R
import com.example.learningdashboard.domain.model.UiText
import com.example.learningdashboard.domain.model.ValidationResult
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(input: String): ValidationResult {
        return when {
            input.length < 8 -> ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.passAtleast8Char)
            )

            !isPasswordValid(input) -> ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.passAtleast1Letter1Digit)
            )

            else -> ValidationResult(
                successful = true,
                errorMessage = null
            )
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.any { it.isDigit() } &&
                password.any { it.isLetter() }
    }
}