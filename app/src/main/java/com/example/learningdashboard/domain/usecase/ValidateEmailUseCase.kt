package com.example.learningdashboard.domain.usecase

import android.util.Patterns
import com.example.learningdashboard.R
import com.example.learningdashboard.domain.model.UiText
import com.example.learningdashboard.domain.model.ValidationResult
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(input: String): ValidationResult {
        if (input.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.emailCanNotBeBlank)
            )
        }

        if (!isEmailValid(input)) {
            return ValidationResult(
                successful = false,
                errorMessage = UiText.StringResource(R.string.emailNotValid)
            )
        }

        return ValidationResult(
            successful = true,
            errorMessage = null
        )
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}