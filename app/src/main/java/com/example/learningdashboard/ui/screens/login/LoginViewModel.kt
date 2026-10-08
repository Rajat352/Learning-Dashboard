package com.example.learningdashboard.ui.screens.login

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.example.learningdashboard.domain.model.UiText
import com.example.learningdashboard.domain.usecase.ValidateEmailUseCase
import com.example.learningdashboard.domain.usecase.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Stable
data class UiState(
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val isPasswordVisible: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: LoginScreenAction) {
        when(action) {
            is LoginScreenAction.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        email = action.email
                    )
                }
                validateEmail()
            }

            is LoginScreenAction.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = action.pass
                    )
                }
                validatePass()
            }

            is LoginScreenAction.PasswordVisibilityToggle -> {
                _uiState.update {
                    it.copy(
                        isPasswordVisible = action.isVisible
                    )
                }
            }

            is LoginScreenAction.Submit -> {
                if (validateEmail() && validatePass()) {
                    // Todo
                }
            }
        }
    }

    private fun validateEmail(): Boolean {
        val result = validateEmailUseCase(uiState.value.email)
        _uiState.update {
            it.copy(
                emailError = result.errorMessage
            )
        }
        return result.successful
    }

    private fun validatePass(): Boolean {
        val result = validatePasswordUseCase(uiState.value.password)
        _uiState.update {
            it.copy(
                passwordError = result.errorMessage
            )
        }
        return result.successful
    }
}